#!/usr/bin/python

import ProducerConsumer as pc
import beanstalkc
from bs4 import BeautifulSoup as parser
from mangapanda import MangaPandaPageScraper
import requests
import logging
import ConfigParser
import pickle
import mangadb as db
from models import *

class Consumer:
	def __init__(self, host, port):
		self.page_beanstalk = beanstalkc.Connection(host=host, port=port)
		self.page_beanstalk.watch('page_updates')

		self.image_download_beanstalk = beanstalkc.Connection(host=host, port=port)
		self.image_download_beanstalk.use('image_download')

		self.page_scraper = MangaPandaPageScraper()

	def consume(self):
		while True:
			try:
				job = self.page_beanstalk.reserve(timeout=300)
				if job is None:
					pc.logger.debug("Consumer timed out. Exiting")
					break
				else:
					page = pickle.loads(job.body)
					page._id = db.persist(page)
					pc.logger.info("[PageConsumer] - %s]", page.name)

					single_page_contents = requests.get(pc.base_url + page.url).text
					psr = parser(single_page_contents)

					page.image_url = self.page_scraper.get_image_url(psr)

					pc.logger.debug("Got image url for %s", page.name)
					db.persist(page)

					self.image_download_beanstalk.put(pickle.dumps(page), priority=30)
					
					job.delete()
			except Exception as e:
				pc.logger.error("Error: %s", e)

	def close(self):
		self.page_beanstalk.close()
		self.image_download_beanstalk.close()


def main():
	host, port = pc.get_beanstalk_server()
	consumer = Consumer(host, port)
	consumer.consume()
	consumer.close()

if __name__ == '__main__':
	main()
