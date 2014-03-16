#!/usr/bin/python

import common
import mangadb
import logging
import pickle
from pprint import pprint
from boto import sdb
from bson.objectid import ObjectId

conn = sdb.connect_to_region('us-east-1')
chapter_dom = conn.get_domain('chapters')

def chunks(s, n):
    """Produce `n`-character chunks from `s`."""
    for start in range(0, len(s), n):
        yield s[start:start+n]

series_cache = {}

def find_series(series_id):
	name = series_cache.get(series_id)
	if name is None:
		record = mangadb.prodDb.series.find_one({"_id" : ObjectId(series_id)})
		if record is not None:
			name = record["url_segment"]

		if name is not None:
			series_cache[series_id] = name

	return name


def main():
	cursor = mangadb.prodDb.chapters.find({"has_migrated_to_sdb" : {"$exists" : False} })
	if cursor is None:
		common.logger.info("Didnt receive any chapters")
	else:
		common.logger.info("Got %s chapters", cursor.count())

		current_batch = {}
		for chapter_record in cursor:
			chapter_id = str(chapter_record.get("_id"))

			# TODO: Need to get series_url_segment, too!

			record = {}
			record["chapter_id"] = chapter_id
			record["series_id"] = str(chapter_record.get("series_id"))
			record["name"] = chapter_record.get("name")
			record["title"] = chapter_record.get("title")
			record["release_date"] = str(chapter_record.get("release_date")).rjust(15, '0')
			record["time_created"] = str(chapter_record.get("time_created")).rjust(15, '0')
			record["sequence_number_sort"] = str(chapter_record.get("sequence_number")).rjust(15, '0')
			record["sequence_number"] = chapter_record.get("sequence_number")

			record["series_url_segment"] = find_series(record["series_id"])

			current_batch[chapter_id] = record

			if len(current_batch) >= 25:
				print current_batch.keys()
				chapter_dom.batch_put_attributes(current_batch)
				for key in current_batch.keys():
					retval = mangadb.prodDb.chapters.update({"_id" : ObjectId(key)} , {"$set" : {"has_migrated_to_sdb" : True}})

				current_batch = {}				

		print current_batch.keys()
		chapter_dom.batch_put_attributes(current_batch)
		for key in current_batch.keys():
			mangadb.prodDb.chapters.update({"_id" : ObjectId(key)} , {"$set" : {"has_migrated_to_sdb" : True}})

if __name__ == '__main__':
	main()