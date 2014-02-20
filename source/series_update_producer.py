#!/usr/bin/python

import ProducerConsumer as pc
import beanstalkc
from mangadb import db
import logging
import pickle

class Producer:
	def __init__(self, host, port):
		self.path = '/alphabetical'
		self.beanstalk = beanstalkc.Connection(host=host, port=port)
		self.beanstalk.use('series_updates')

	def produce(self):
		series_cursor = db.series.find()
		if series_cursor is None:
			pc.logger.info("Didnt receive any series")
		else:
			pc.logger.info("Got %s series", series_cursor.count())
			for series_record in series_cursor:
				self.beanstalk.put(pickle.dumps(series_record), priority=10)

	def close(self):
		self.beanstalk.close()

def main():
	host, port = pc.get_beanstalk_server()
	producer = Producer(host, port)
	producer.produce()
	producer.close()
	exit()

if __name__ == '__main__':
	main()
