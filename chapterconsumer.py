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
		self.chapter_beanstalk = beanstalkc.Connection(host=host, port=port)
		self.chapter_beanstalk.watch('chapter')

		self.page_beanstalk = beanstalkc.Connection(host=host, port=port)
		self.page_beanstalk.use('page')
		
		self.page_scraper = MangaPandaPageScraper()


	def consume(self):
		while True:
			try:
				job = self.chapter_beanstalk.reserve(timeout=300)
				if job is None:
					pc.logger.debug("Consumer timed out. Exiting")
					break
				else:
					chapter = pickle.loads(job.body)
					chapter._id = db.persist(chapter)
					pc.logger.info("[ChapterConsumer] - %s]", chapter.name)

					page_contents = requests.get(pc.base_url + chapter.url).text
					psr = parser(page_contents)

					pages = self.page_scraper.get_pages_url(psr)

					if pages is None:
						pc.logger.debug("No pages found for %s", chapter.name)
					else:
						pc.logger.info("Got %s pages for %s", len(pages), chapter.name)
						for page in pages:
							page.chapter_id = chapter._id
							self.page_beanstalk.put(pickle.dumps(page), priority=30)

					job.delete()
			except Exception as e:
				pc.logger.error("Error: %s", e)

	def close(self):
		self.chapter_beanstalk.close()
		self.page_beanstalk.close()


def main():
	host, port = pc.get_beanstalk_server()
	consumer = Consumer(host, port)
	consumer.consume()
	consumer.close()
	exit()

if __name__ == '__main__':
	main()
