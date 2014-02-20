#!/usr/bin/python

import ProducerConsumer as pc
import beanstalkc
from bs4 import BeautifulSoup as parser
from mangapanda import MangaPandaSeriesScraper
import mangadb as db
import requests
import logging
import pickle

class Producer:
	def __init__(self, host, port):
		self.path = '/alphabetical'
		self.beanstalk = beanstalkc.Connection(host=host, port=port)
		self.beanstalk.use('series')
		self.series_scraper = MangaPandaSeriesScraper()

	def produce(self):
		page_contents = requests.get(pc.base_url + self.path).text
		series_list = self.series_scraper.get_manga_list(parser(page_contents))
		if series_list is None:
			pc.logger.info("Didnt receive any series")
		else:
			pc.logger.info("Got %s series", len(series_list))
			id_counter = 0 
			for series in series_list:
				self.beanstalk.put(pickle.dumps(series), priority=10)

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
