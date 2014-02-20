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
from mangadb import db
from sets import Set

class Consumer:
	def __init__(self, host, port):
		self.series_beanstalk = beanstalkc.Connection(host=host, port=port)
		self.series_beanstalk.watch('series_updates')

		self.chapter_beanstalk = beanstalkc.Connection(host=host, port=port)
		self.chapter_beanstalk.use('chapter_updates')

		self.chapter_scraper = MangaPandaChapterScraper()

	def find_new_chapters(self, incoming, existing_urls):
		new_chapters = []
		for chapter in incoming:
			if chapter.url not in existing_urls:
				new_chapters.append(chapter)
		return new_chapters

	def consume(self):
		while True:
			try:
				job = self.series_beanstalk.reserve(timeout=300)
				if job is None:
					pc.logger.debug("Consumer timed out. Exiting")
					break;
				else:
					series_record = pickle.loads(job.body)
					series_id = series_record["_id"]
					series_name = series_record["name"]

					existing_chapters = db.chapters.find( { "series_id" : series_id } )
					existing_chapter_urls = Set([c["url"] for c in existing_chapters])

					series_contents = requests.get(pc.base_url + series_record["url"]).text
					psr = parser(series_contents)
					chapters = self.chapter_scraper.get_chapter_urls(psr);
					if chapters is None:
						pc.logger.info("Didnt receive any chapters for %s", series_name)
					elif existing_chapters is not None and len(chapters) <= existing_chapters.count():
						pc.logger.info("No new chapters for %s", series_name)
					else:
						new_chapters = self.find_new_chapters(chapters, existing_chapter_urls);
						pc.logger.info("Got %s new chapters for %s", len(new_chapters), series_name)
						for c in new_chapters:
							c.series_id = series_id

							if c.name.startswith(series_name):
								c.sequence_number = int(chapter_name[len(series_name):])

							self.beanstalk.put(pickle.dumps(c), priority=20)

					job.delete()
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
