#!/usr/bin/python

import ProducerConsumer as pc
from mangadb import db
from bson.objectid import ObjectId
from models import MangaPage
import logging
from datetime import datetime
import calendar

def main():
	series_cursor = db.series.find()
	for series_record in series_cursor:
		id = series_record.get('_id')

		chapters_cursor = db.chapters.find({'series_id' : id})
		if chapters_cursor is None or chapters_cursor.count() == 0:
			pc.logger.warn("Removing %s", series_record.get('name'))
			db.series.remove({'_id' : id})

if __name__ == '__main__':
	main()
