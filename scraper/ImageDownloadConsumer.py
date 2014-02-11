#!/usr/bin/python

import beanstalkc
import sys
import requests
import mangadb as db
import logging
import ProducerConsumer as pc
import mimetypes
import pickle

def add_image(filename, image_url):
	"""
	Kudos: http://cuppster.com/2013/03/12/serving-images-with-mongodb-gridfs-and-flask/
	"""
	r = requests.get(image_url, stream=True)
	return db.persist_raw(r.raw, filename, mimetypes.guess_type(image_url)[0])

class Consumer:
	def __init__(self, host, port):
		self.beanstalk = beanstalkc.Connection(host=host, port=port)
		self.beanstalk.watch('image_download')

	def consume(self):
		while True:
			try:
				job = self.beanstalk.reserve(timeout=300)
				if job is None:
					pc.logger.debug("Consumer timed out. Exiting")
					break
				else:
					page = pickle.loads(job.body)
					if page.image_url is None:
						pc.logger.error("Dont have image_url for %s - %s. Ignoring", page.name, page.url)
					else:
						pc.logger.info("[ImageDownloader] - %s - %s]", page.name, page.image_url)

						page.image_id = add_image(page.name, page.image_url)
						db.persist(page)

						pc.logger.info("[ImageDownloader] - got image %s - %s", page.name, page.image_url)

					job.delete()
			except Exception as e:
				pc.logger.error("Error: %s", e)

	def close(self):
		self.beanstalk.close()

def main():
	host, port = pc.get_beanstalk_server()
	consumer = Consumer(host, port)
	consumer.consume()
	consumer.close()
	exit()

if __name__ == '__main__':
	main()