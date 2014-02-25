#!/usr/bin/python

import ProducerConsumer as pc
import beanstalkc
from bs4 import BeautifulSoup as parser
from mangapanda import MangaPandaChapterScraper
import requests
import logging
import ConfigParser
import pickle
from models import *
import mangadb as db

class Consumer:
	def __init__(self, host, port):
		self.series_beanstalk = beanstalkc.Connection(host=host, port=port)
		self.series_beanstalk.watch('series_from_db')

		self.chapter_scraper = MangaPandaChapterScraper()

	def consume(self):
		while True:
			try:
				job = self.series_beanstalk.reserve(timeout=300)
				if job is None:
					pc.logger.debug("Consumer timed out. Exiting")
					break;
				else:
					series = pickle.loads(job.body)
					pc.logger.info("[SeriesConsumer] - [%s]", series.name)

					series_contents = requests.get(pc.base_url + series.url).text
					psr = parser(series_contents)

					chapters = self.chapter_scraper.get_chapter_urls(psr);

					if chapters is None:
						pc.logger.info("Didnt receive any chapters")
					else:
						pc.logger.info("Got %s chapters", len(chapters))
						for chapter in chapters:
							pc.logger.info("Got %s %s", chapter.title, chapter.url)
							db.pages.update( {"url" : chapter.url } , { "$set" : {"title" : chapter.title} } , multi=True)

					# job.delete()
			except Exception as e:
				pc.logger.error("Error: %s", e)

	def close(self):
		self.series_beanstalk.close()
		self.chapter_beanstalk.close()

def main():
	host, port = pc.get_beanstalk_server()
	consumer = Consumer(host, port)
	consumer.consume()
	consumer.close()
	exit()

if __name__ == '__main__':
	main()
