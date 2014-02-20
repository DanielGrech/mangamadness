#!/usr/bin/python

import ProducerConsumer as pc
from mangadb import db
from bson.objectid import ObjectId
from models import MangaPage
import logging
from datetime import datetime
import calendar
import re
import string

def main():
	chapters_cursor = db.chapters.find()
	count = chapters_cursor.count()
	counter = 1
	pc.logger.info("Found %s chapters", count)
	for chapter_record in chapters_cursor:
		chapter_id = chapter_record.get('_id')
		series_id = chapter_record.get("series_id")
		db.pages.update( {"chapter_id" : chapter_id } , { "$set" : {"series_id" : series_id} } , multi=True)

		pc.logger.info("Done %s / %s", counter, count)
		counter = counter + 1

if __name__ == '__main__':
	main()
