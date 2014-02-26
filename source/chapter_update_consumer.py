#!/usr/bin/python

import common
import beanstalkc
from bs4 import BeautifulSoup as parser
from mangapanda import MangaPandaPageScraper
import requests
import logging
import pickle
import mangadb
from models import *

class Consumer:
	def __init__(self, host, port):
		self.chapter_beanstalk = beanstalkc.Connection(host=host, port=port)
		self.chapter_beanstalk.watch('chapter_updates')

		self.page_beanstalk = beanstalkc.Connection(host=host, port=port)
		self.page_beanstalk.use('page_updates')
		
		self.page_scraper = MangaPandaPageScraper()


	def consume(self):
		while True:
			try:
				job = self.chapter_beanstalk.reserve(timeout=300)
				if job is None:
					common.logger.debug("Consumer timed out. Exiting")
					break
				else:
					chapter = pickle.loads(job.body)
					chapter._id = mangadb.persist(chapter, mangadb.sourceDb)
					common.logger.info("[ChapterConsumer] - %s]", chapter.name)

					page_contents = requests.get(common.base_url + chapter.url).text
					psr = parser(page_contents)

					pages = self.page_scraper.get_pages_url(psr)

					if pages is None:
						common.logger.debug("No pages found for %s", chapter.name)
					else:
						common.logger.info("Got %s pages for %s", len(pages), chapter.name)
						for page in pages:
							page.chapter_id = chapter._id
							page.series_id = chapter.series_id
							self.page_beanstalk.put(pickle.dumps(page), priority=30)

					job.delete()
			except Exception as e:
				common.logger.error("Error: %s", e)

	def close(self):
		self.chapter_beanstalk.close()
		self.page_beanstalk.close()


def main():
	host, port = common.get_beanstalk_server()
	consumer = Consumer(host, port)
	consumer.consume()
	consumer.close()
	exit()

if __name__ == '__main__':
	main()
