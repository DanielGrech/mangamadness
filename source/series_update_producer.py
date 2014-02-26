#!/usr/bin/python

import common
import beanstalkc
import mangadb
import logging
import pickle

class Producer:
	def __init__(self, host, port):
		self.path = '/alphabetical'
		self.beanstalk = beanstalkc.Connection(host=host, port=port)
		self.beanstalk.use('series_updates')

	def produce(self):
		series_cursor = mangadb.sourceDb.series.find()
		if series_cursor is None:
			common.logger.info("Didnt receive any series")
		else:
			common.logger.info("Got %s series", series_cursor.count())
			for series_record in series_cursor:
				self.beanstalk.put(pickle.dumps(series_record), priority=10)

	def close(self):
		self.beanstalk.close()

def main():
	host, port = common.get_beanstalk_server()
	producer = Producer(host, port)
	producer.produce()
	producer.close()
	exit()

if __name__ == '__main__':
	main()
