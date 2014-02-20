#!/usr/bin/python

import ProducerConsumer as pc
from mangadb import db
from bson.objectid import ObjectId
from models import MangaPage
import logging
from datetime import datetime
import calendar

def main():
	series_cursor = db.series.find().batch_size(20)
	pc.logger.info("Got %s series", series_cursor.count())
	counter = 1
	for series_record in series_cursor:
		series = {}
		series['_id'] = series_record.get('_id')
		series['name'] = series_record.get('name')
		series['artist'] = series_record.get('artist')
		series['author'] = series_record.get('author')
		series['summary'] = series_record.get('summary')
		series['genres'] = series_record.get('genres')
		series['year_of_release'] = series_record.get('year_of_release')
		series['original_cover_image_url'] = series_record.get('cover_image_url')

		chapters_cursor = db.chapters.find({'series_id' : series['_id']}).batch_size(20)
		if chapters_cursor is not None:
			chapters = []
			for chapter_record in chapters_cursor:
				chapter = {}
				chapter['_id'] = chapter_record.get('_id') 
				chapter['series_id'] = series['_id']
				chapter['name'] = chapter_record.get('name')

				date_added_str = chapter_record.get('date_added')
				if date_added_str is not None:
					dt = datetime.strptime(date_added_str, '%m/%d/%Y')
					chapter['release_date'] = calendar.timegm(dt.utctimetuple())

				pages_cursor = db.pages.find({'chapter_id' : chapter['_id']}).batch_size(50)
				if pages_cursor is not None:
					pages = []
					for page_record in pages_cursor:
						page = {}
						page['_id'] = page_record.get('_id') 
						page['chapter_id'] = chapter['_id']
						page['name'] = page_record.get('name')
						page['original_image_url'] = page_record.get('image_url')
						pages.append(page)

					chapter['pages'] = pages
				else:
					pc.logger.waring('No pages found for series %s chapter %s', series['name'], chapter['name'])

				chapters.append(chapter)

			series['chapters'] = chapters
			db.manga.save(series, manipulate=True)
			pc.logger.info('Finished %s (%s/%s)', series['name'], counter, series_cursor.count())
			counter = counter + 1
		else:
			pc.logger.warning('No chapters found for %s', series)


if __name__ == '__main__':
	main()
