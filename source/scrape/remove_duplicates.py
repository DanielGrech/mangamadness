#!/usr/bin/python

import ProducerConsumer as pc
from mangadb import db
from bson.objectid import ObjectId
from models import MangaPage
import logging
from datetime import datetime
import calendar

def delete_pages_in_chapter(chapter_id):
	pages_cursor = db.pages.find({"chapter_id" : chapter_id})
	if pages_cursor is not None:
		for page_record in pages_cursor:
			db.pages.remove({"_id" : page_record["_id"]})

def delete_chapters_in_series(series_id):
	chapters_cursor = db.chapters.find({"series_id" : series_id})
	if chapters_cursor is not None:
		for chapter_record in chapters_cursor:
			delete_pages_in_chapter(chapter_record["_id"])
			db.chapters.remove({"_id" : chapter_record["_id"]})


def main():
	aggregate = db.series.aggregate([
			{ "$group" : { "_id" : "$name", "total" : { "$sum" : 1 } } },     
			{ "$match" : { "total" : { "$gte" : 2 } } },     
			{ "$sort" : {"total" : -1} }
	])

	if aggregate is not None and aggregate.get("result") is not None:
		result = aggregate.get("result")
		for duplicate in result:
			series_cursor = db.series.find({"name" : duplicate["_id"]})
			if (series_cursor is not None) and series_cursor.count() > 1:
				is_first = True
				for series_record in series_cursor:
					if not is_first:
						pc.logger.info("Deleting %s [%s]", series_record["name"], series_record["_id"])
						delete_chapters_in_series(series_record["_id"])
						db.series.remove({"_id" : series_record["_id"]})
					else:
						pc.logger.info("Keeping %s [%s]", series_record["name"], series_record["_id"])
						is_first = False

if __name__ == '__main__':
	main()
