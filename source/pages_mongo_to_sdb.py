#!/usr/bin/python

import common
import beanstalkc
import mangadb
import logging
import pickle
from pprint import pprint
from boto import sdb
from bson.objectid import ObjectId

class Producer:
	def __init__(self, host, port):
		self.beanstalk = beanstalkc.Connection(host=host, port=port)
		self.beanstalk.use('page_migration')

	def produce(self):
		cursor = mangadb.prodDb.pages.find({"has_migrated_to_sdb" : {"$exists" : False} })
		if cursor is None:
			common.logger.info("Didnt receive any pages")
		else:
			common.logger.info("Got %s pages", cursor.count())

		count = 0
		current_batch = {}
		for page_record in cursor:
			page_id = str(page_record.get("_id"))

			record = {}
			record["page_id"] = page_id
			record["chapter_id"] = str(page_record.get("chapter_id"))
			record["image_url"] = page_record.get("image_url")
			record["sequence_number"] = int(page_record.get("sequence_number"))
			record["sequence_number_sort"] = str(record["sequence_number"]).rjust(15, '0')

			current_batch[page_id] = record

			if len(current_batch) >= 25:
				count = count + 25
				common.logger.debug("Pushing %s into queue", count)
				self.beanstalk.put(pickle.dumps(current_batch), priority=10)
				current_batch = {}				

		self.beanstalk.put(pickle.dumps(current_batch), priority=10)


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