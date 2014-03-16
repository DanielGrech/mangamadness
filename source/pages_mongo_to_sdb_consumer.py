#!/usr/bin/python

import common
import beanstalkc
from bs4 import BeautifulSoup as parser
from mangapanda import MangaPandaChapterScraper
import requests
import logging
import ConfigParser
import pickle
from models import *
import mangadb
from sets import Set
from boto import sdb
from bson.objectid import ObjectId

conn = sdb.connect_to_region('us-east-1')
pages_dom = conn.get_domain('pages')

class Consumer:
	def __init__(self, host, port):
		self.beanstalk = beanstalkc.Connection(host=host, port=port)
		self.beanstalk.watch('page_migration')

	def consume(self):
		while True:
			try:
				job = self.beanstalk.reserve(timeout=300)
				if job is None:
					common.logger.debug("Consumer timed out. Exiting")
					break;
				else:
					batch = pickle.loads(job.body)

					pages_dom.batch_put_attributes(batch)

					common.logger.debug(batch.keys())

					for key in batch.keys():
						mangadb.prodDb.pages.update({"_id" : ObjectId(key)} , {"$set" : {"has_migrated_to_sdb" : True}})

					job.delete()
			except Exception as e:
				common.logger.error("Error: %s", e)

	def close(self):
		self.beanstalk.close()

def main():
	host, port = common.get_beanstalk_server()
	consumer = Consumer(host, port)
	consumer.consume()
	consumer.close()
	exit()

if __name__ == '__main__':
	main()
