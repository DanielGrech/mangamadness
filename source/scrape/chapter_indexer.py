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
		series_name = series_record.get("name")

		chapters_cursor = db.chapters.find({'series_id' : id})
		if chapters_cursor is not None:
			for chapter_record in chapters_cursor:
				chapter_name = chapter_record.get("name")
				if chapter_name.startswith(series_name):
					chapter_record["sequence_number"] = int(chapter_name[len(series_name):])
					db.chapters.save(chapter_record, manipulate=True)
				else:
					pc.logger.debug("O NO: %s %s", series_name, chapter_name)

if __name__ == '__main__':
	main()
