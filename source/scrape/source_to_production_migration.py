#!/usr/bin/python

import common
from bson.objectid import ObjectId
import logging
import mangadb
import beanstalkc
import pickle
from datetime import datetime
import calendar
import time


def migrate_series():
	cursor = mangadb.sourceDb.series.find()
	if cursor is None:
		common.logger.info("Series cursor was empty. Returning")
		return

	count = cursor.count()
	common.logger.info("Got %s series to migrate", count)

	counter = 0
	for source_record in cursor:
		prod_record = {}
		prod_record['_id'] = source_record.get('_id')
		prod_record['name'] = source_record.get('name')
		prod_record['author'] = source_record.get('author')
		prod_record['artist'] = source_record.get('artist')
		prod_record['summary'] = source_record.get('summary')
		prod_record['url_segment'] = source_record.get('url_segment')
		prod_record['cover_image_url'] = source_record.get('cover_image')
		prod_record['genres'] = source_record.get('genres')
		prod_record['time_created'] = calendar.timegm(time.gmtime())

		source_release_year = source_record.get('year_of_release');
		if source_release_year is not None and source_release_year.isdigit():
			prod_record['year_of_release'] = int(source_release_year)

		mangadb.prodDb.series.save(prod_record, manipulate=True)
		mangadb.sourceDb.series.update({"_id" : source_record.get('_id')} , {"$set" : {"has_migrated" : True}})

		common.logger.info("Series %s / %s", counter, count)
		counter = counter + 1


def migrate_chapters():
	cursor = mangadb.sourceDb.chapters.find()
	if cursor is None:
		common.logger.info("Chapters cursor was empty. Returning")
		return

	count = cursor.count()
	common.logger.info("Got %s series to migrate", count)

	counter = 0
	for source_record in cursor:
		prod_record = {}
		prod_record['_id'] = source_record.get('_id')
		prod_record['series_id'] = source_record.get('series_id')
		prod_record['name'] = source_record.get('name')
		prod_record['title'] = source_record.get('title')
		prod_record['sequence_number'] = source_record.get('sequence_number')
		prod_record['time_created'] = calendar.timegm(time.gmtime())
		
		date_added_str = prod_record.get('date_added')
		if date_added_str is not None:
			dt = datetime.strptime(date_added_str, '%m/%d/%Y')
			prod_record['release_date'] = calendar.timegm(dt.utctimetuple())

		mangadb.prodDb.chapters.save(prod_record, manipulate=True)
		mangadb.sourceDb.chapters.update({"_id" : source_record.get('_id')} , {"$set" : {"has_migrated" : True}})

		common.logger.info("Chapter %s / %s", counter, count)
		counter = counter + 1

def migrate_pages():
	cursor = mangadb.sourceDb.pages.find()
	if cursor is None:
		common.logger.info("Pages cursor was empty. Returning")
		return

	count = cursor.count()
	common.logger.info("Got %s pages to migrate", count)

	counter = 0
	for source_record in cursor:
		prod_record = {}
		prod_record['_id'] = source_record.get('_id')
		prod_record['series_id'] = source_record.get('series_id')
		prod_record['chapter_id'] = source_record.get('chapter_id')
		prod_record['sequence_number'] = source_record.get('name')
		prod_record['image_url'] = source_record.get('image')
		prod_record['time_created'] = calendar.timegm(time.gmtime())
	
		mangadb.prodDb.pages.save(prod_record, manipulate=True)
		mangadb.sourceDb.pages.update({"_id" : source_record.get('_id')} , {"$set" : {"has_migrated" : True}})

		common.logger.info("Page %s / %s", counter, count)
		counter = counter + 1

def main():
	common.logger.info("Migrating series...")
	migrate_series();

	common.logger.info("Migrating chapters...")
	migrate_chapters();

	common.logger.info("Migrating pages...")
	migrate_pages();

if __name__ == '__main__':
	main()
