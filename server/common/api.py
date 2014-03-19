from boto import sdb
from redis import StrictRedis
import pickle

class PickledRedis(StrictRedis):
    def get(self, name):
        pickled_value = super(PickledRedis, self).get(name)
        if pickled_value is None:
            return None
        return pickle.loads(pickled_value)

    def set(self, name, value, ex=None, px=None, nx=False, xx=False):
        return super(PickledRedis, self).set(name, pickle.dumps(value), ex, px, nx, xx)

redis = PickledRedis(host='localhost', port=6379, db=0)

sdb_conn = sdb.connect_to_region('us-east-1')
series_dom = sdb_conn.get_domain('series')
chapters_dom = sdb_conn.get_domain('chapters')
pages_dom = sdb_conn.get_domain('pages')

def clean_series(input):
	if input is not None and isinstance(input, dict):
		retval = {}
		retval["id"] = input["series_id"]
		retval["name"] = input["name"]
		retval["url_segment"] = input["url_segment"]
		retval["artist"] = input["artist"]
		retval["author"] = input["author"]
		retval["cover_image_url"] = input["cover_image_url"]
		retval["genres"] = input["genres"]
		retval["time_created"] = int(input["time_created"])

		year_of_release = input["year_of_release"]
		if year_of_release is not None and year_of_release != "None":
			retval["year_of_release"] = int(year_of_release)
		

		summary = ""
		summary_part = 0
		while True:
			part = "summary_{}".format(summary_part)
			if part in input:
				to_add = None
				if isinstance(input[part], unicode):
					to_add = input[part].encode('ascii', 'ignore')
				elif isinstance(input[part], str):
					to_add = input[part].decode('utf-8', 'ignore').encode('ascii', 'ignore')
				else:
					to_add = input[part]

				summary += to_add
				summary_part = summary_part + 1
			else:
				break
		retval["summary"] = summary

		return retval

def clean_chapter(input):
	if input is not None and isinstance(input, dict):
		retval = {}
		retval["id"] = input["chapter_id"]
		retval["name"] = input["name"]
		retval["title"] = input["title"]
		retval["release_date"] = int(input["release_date"])
		retval["sequence_number"] = int(input["sequence_number"])
		retval["series_id"] = input["series_id"]
		retval["time_created"] = int(input["time_created"])
		return retval


def clean_page(input):
	if input is not None and isinstance(input, dict):
		retval = {}
		retval["id"] = input["page_id"]
		retval["image_url"] = input["image_url"]
		retval["chapter_id"] = input["chapter_id"]
		retval["sequence_number"] = int(input["sequence_number"])
		return retval

def search_series(query, limit, offset, updated_since):
	redis_key = "search_series_query_{}_list_lim_{}_offset_{}_since_{}".format(query, limit, offset, updated_since)

	retval = redis.get(redis_key)

	if retval is None:
		where = "url_segment like '{}%'".format(query)
		order_by = "url_segment"

		query = """
			SELECT * FROM `series` WHERE {} ORDER BY {} LIMIT {}
		""".format(where, order_by, limit)

		rs = None
		if offset == 0:
			rs = series_dom.select(query, max_items=limit)
		else:
			rs = series_dom.select("""
				SELECT count(*) FROM `series` WHERE {} ORDER BY {} LIMIT {}
			""".format(where, order_by, offset), max_items=1)

			if rs:
				for r in rs:
					pass
				rs = series_dom.select(query, max_items=limit, next_token=rs.next_token)

		retval = []
		if rs:
			for r in rs:
				series = clean_series(r)
				# Persist it to redis incase we want to get a single series
				redis.set(series["name"], series)
				retval.append(series)

		redis.set(redis_key, retval, ex=60*60*24*7)

	return retval		


def get_series_list(limit, offset, updated_since):
	redis_key = "get_series_list_lim_{}_offset_{}_since_{}".format(limit, offset, updated_since)

	retval = redis.get(redis_key)

	if retval is None:
		where = "url_segment is not null"
		order_by = "url_segment"

		query = """
			SELECT * FROM `series` WHERE {} ORDER BY {} LIMIT {}
		""".format(where, order_by, limit)

		rs = None
		if offset == 0:
			rs = series_dom.select(query, max_items=limit)
		else:
			rs = series_dom.select("""
				SELECT count(*) FROM `series` WHERE {} ORDER BY {} LIMIT {}
			""".format(where, order_by, offset), max_items=1)

			if rs:
				for r in rs:
					pass
				rs = series_dom.select(query, max_items=limit, next_token=rs.next_token)

		retval = []
		if rs:
			for r in rs:
				series = clean_series(r)

				# Persist it to redis incase we want to get a single series
				redis.set(series["name"], series)
				retval.append(series)

		redis.set(redis_key, retval)

	return retval

def get_series(series_name):
	series = redis.get(series_name)
	if series is None:
		rs = series_dom.select("""
			SELECT * FROM `series` WHERE url_segment = '{}' LIMIT 1
		""".format(series_name))
		if rs:
			for r in rs:
				record = clean_series(r)
				redis.set(series_name, record)
				return record
	else:
		return series

def get_chapter_list(series_name, limit, offset, updated_since):
	redis_key = "get_chapter_list_{}_lim_{}_offset_{}_since_{}".format(series_name, limit, offset, updated_since)

	retval = redis.get(redis_key)

	if retval is None:
		series = get_series(series_name)
		if series:
			where = """
				sequence_number_sort IS NOT NULL 
				INTERSECTION series_id = '{}'
			""".format(series["id"])
			order_by = "sequence_number_sort desc"

			query = """
				SELECT * FROM `chapters` WHERE {} ORDER BY {} LIMIT {}
			""".format(where, order_by, limit)

			rs = None
			if offset == 0:
				rs = series_dom.select(query, max_items=limit)
			else:
				rs = series_dom.select("""
					SELECT count(*) FROM `chapters` WHERE {} ORDER BY {} LIMIT {}
				""".format(where, order_by, offset), max_items=1)

				if rs:
					for r in rs:
						pass
					rs = series_dom.select(query, max_items=limit, next_token=rs.next_token)

			retval = []
			if rs:
				for r in rs:
					chapter = clean_chapter(r)
					if chapter:
						key = "series_{}_chapter_{}".format(series["url_segment"], chapter["sequence_number"])
						redis.set(key, chapter)
						retval.append(chapter)
			redis.set(redis_key, retval)

	return retval

def get_chapter_by_id(chapter_id):
	redis_key = "pages_in_chapter_{}".format(chapter_id)
	pages = redis.get(redis_key)
	if pages is None:
		rs = pages_dom.select("""
			SELECT * FROM `pages` 
			WHERE 
				chapter_id = '{}' 
					INTERSECTION 
				sequence_number_sort IS NOT NULL
			ORDER BY sequence_number_sort ASC
		""".format(chapter_id))

		pages = []
		if rs:
			for r in rs:
				page = clean_page(r)
				pages.append(page)

		redis.set(redis_key, pages)

	return pages

def get_chapter(series_name, chapter_number):
	redis_key = "series_{}_chapter_{}".format(series_name, chapter_number)
	chapter = redis.get(redis_key)
	if chapter is None:
		pass

	if chapter is not None:
		return get_chapter_by_id(chapter["id"])




