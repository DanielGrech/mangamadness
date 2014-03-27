from google.appengine.api import memcache as cache
from google.appengine.ext.db import Query
from common import model

def search_series(query, limit, offset, updated_since):
	cache_key = "search_series_query_{}_list_lim_{}_offset_{}_since_{}".format(query, limit, offset, updated_since)

	retval = cache.get(cache_key)


	if retval is None:
		retval = model.Series.all()
			.filter('url_segment >= ', quer)
			.filter('url_segment < ', query + u"\ufffd")
			.order('url_segment')
			.fetch(limit = limit, offset = offset)

		if retval:
			for s in retval:
				cache.set(s.name, s)
			cache.set(cache_key, retval)

	return retval


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
				cache.set(series["name"], series)
				retval.append(series)

		cache.set(cache_key, retval, time=60*60*24*7)

	return retval		


def get_series_list(limit, offset, updated_since):
	cache_key = "get_series_list_lim_{}_offset_{}_since_{}".format(limit, offset, updated_since)

	retval = cache.get(cache_key)

	if retval is None:
		retval = model.Series.all().order('url_segment').fetch(limit = limit, offset = offset)

		if retval:
			for s in retval:
				cache.set(s.name, s)
			cache.set(cache_key, retval)

	return retval

def get_series(series_name):
	series = cache.get(series_name)
	if series is None:
		series = model.Series.all().filter('url_segment', series_name).get()
		if series:
			cache.set(series_name, series)
	return series

def get_chapter_list(series_name, limit, offset, updated_since):
	cache_key = "get_chapter_list_{}_lim_{}_offset_{}_since_{}".format(series_name, limit, offset, updated_since)

	retval = cache.get(cache_key)

	if retval is None:
		series = get_series(series_name)
		if series:
			retval = model.Chapter.all()
				.filter('series_id', series.id)
				.sort('-sequence_number')
				.fetch(limit = limit, offset = offset)

			if retval:
				for c in retval:
					key = "series_{}_chapter_{}".format(series.url_segment, c.sequence_number)
					cache.set(key, chapter)	
				cache.set(cache_key, retval)

	return retval

def get_chapter_by_id(chapter_id):
	cache_key = "pages_in_chapter_{}".format(chapter_id)
	pages = cache.get(cache_key)
	if pages is None:
		pages = model.Page.all().filter('chapter_id', chapter_id).sort('-sequence_number').fetch()
		if pages:
			cache.set(cache_key, pages)
	return pages

def get_chapter(series_name, chapter_number):
	cache_key = "series_{}_chapter_{}".format(series_name, chapter_number)
	chapter = cache.get(cache_key)
	if chapter is None:
		series = get_series(series_name)
		if series:
			chapter = model.Chapter.all()
				.filter('series_id', series.id)
				.filter('sequence_number', chapter_number)
				.get()
			if chapter:
				return get_chapter_by_id(chapter.id)

	if chapter:
		return get_chapter_by_id(chapter.id)




