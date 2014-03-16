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
	return input

def clean_chapter(input):
	return input

def clean_page(input):
	return input


def get_series_list(limit, offset, updated_since):
	redis_key = """
		get_series_list_lim_{}_offset_{}_since_{}
	""".format(limit, offset, updated_since)

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
				retval.append(clean_series(r))

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
	redis_key = """
		get_chapter_list_{}_lim_{}_offset_{}_since_{}
	""".format(series_name, limit, offset, updated_since)

	retval = redis.get(redis_key)

	if retval is None:
		series = get_series(series_name)
		if series:
			where = """
				sequence_number_sort IS NOT NULL 
				INTERSECTION series_id = '{}'
			""".format(series["series_id"])
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
					retval.append(clean_chapter(r))
			redis.set(redis_key, retval)

	return retval







