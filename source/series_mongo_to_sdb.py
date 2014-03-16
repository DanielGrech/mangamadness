#!/usr/bin/python

import common
import mangadb
import logging
import pickle
from pprint import pprint
from boto import sdb

conn = sdb.connect_to_region('us-east-1')
series_dom = conn.get_domain('series')

def chunks(s, n):
    """Produce `n`-character chunks from `s`."""
    for start in range(0, len(s), n):
        yield s[start:start+n]

def main():
	series_cursor = mangadb.prodDb.series.find()
	if series_cursor is None:
		common.logger.info("Didnt receive any series")
	else:
		common.logger.info("Got %s series", series_cursor.count())

		current_batch = {}
		for series_record in series_cursor:
			series_id = str(series_record.get("_id"))

			record = {}
			record["series_id"] = series_id
			record["name"] = series_record.get("name")
			record["artist"] = series_record.get("artist")
			record["author"] = series_record.get("author")
			record["cover_image_url"] = series_record.get("cover_image_url")
			record["year_of_release"] = str(series_record.get("year_of_release"))
			record["url_segment"] = series_record.get("url_segment")
			record["time_created"] = str(series_record.get("time_created")).rjust(15, '0')

			genres = series_record.get("genres")
			if genres is not None:
				record["genres"] = genres

			summary = series_record.get('summary')
			size = 0 if summary is None else len(summary.encode('utf-8'))

			chunk_num = 0
			for chunk in chunks(summary.encode('utf-8'), 800):
				record["summary_" + str(chunk_num)] = chunk.decode('unicode-escape')
				chunk_num = chunk_num + 1

			current_batch[series_id] = record

			if len(current_batch) >= 25:
				print current_batch.keys()
				series_dom.batch_put_attributes(current_batch)
				current_batch = {}

		print current_batch.keys()
		series_dom.batch_put_attributes(current_batch)

if __name__ == '__main__':
	main()