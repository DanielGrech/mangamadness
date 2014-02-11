#!/usr/bin/python

import ProducerConsumer as pc
import beanstalkc
from bs4 import BeautifulSoup as parser
from mangapanda import MangaPandaSeriesScraper
from mangadb import db
from models import MangaPage
import requests
import logging
import pickle

class Producer:
	def __init__(self, host, port):
		self.beanstalk = beanstalkc.Connection(host=host, port=port)
		self.beanstalk.use('image_download')

	def produce(self):
		cursor = db.pages.find()
		pc.logger.info("Got %s pages", cursor.count())
		for pageRecord in cursor:
			page = MangaPage(pageRecord['name'], pageRecord['url'])
			page._id = pageRecord['_id']
			page.chapter_id = pageRecord['chapter_id']
			page.image_id = pageRecord['image_id']
			page.image_url = pageRecord['image_url']

			self.beanstalk.put(pickle.dumps(page), 50)

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
