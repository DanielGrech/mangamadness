#!/usr/bin/python

import ProducerConsumer as pc
from mangadb import prodDb
from bson.objectid import ObjectId
from models import MangaSeries
import logging
from datetime import datetime
import beanstalkc
import pickle
import calendar

def main():
	host, port = pc.get_beanstalk_server()
	beanstalk = beanstalkc.Connection(host=host, port=port)
	beanstalk.use('series_from_db')

	series_cursor = db.series.find()
	for series_record in series_cursor:
		series = MangaSeries(series_record.get('name'), series_record.get('url'))
		series._id = series_record.get('_id')
		series.summary = series_record.get('summary')
		series.author = series_record.get('author')
		series.artist = series_record.get('artist')
		series.year_of_release = series_record.get('year_of_release')
		series.url_segment = series_record.get('url_segment')
		series.genres = series_record.get('genres')
		series.cover_image_url = series_record.get('cover_image_url')
		series.cover_image = series_record.get('cover_image')

		beanstalk.put(pickle.dumps(series), priority=5)

if __name__ == '__main__':
	main()
