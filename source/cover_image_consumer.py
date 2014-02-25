#!/usr/bin/python

import beanstalkc
import sys
import requests
import os
import mangadb as db
import logging
import ProducerConsumer as pc
import mimetypes
import pickle
from boto.s3.connection import S3Connection
from boto.s3.key import Key

class Consumer:
	def __init__(self, host, port, bucket):
		self.beanstalk = beanstalkc.Connection(host=host, port=port)
		self.beanstalk.watch('series_from_db')

		self.bucket = bucket

	def download_image(self, image_url):
		fn = 'cache/' + str(os.getpid())
		with open(fn, 'wb') as handle:
		    request = requests.get(image_url, stream=True)
		    for block in request.iter_content(1024):
		        if not block:
		            break

		        handle.write(block)
		return fn

	def add_image(self, keyname, image_url):
		filename = self.download_image(image_url)

		key = Key(self.bucket)
		key.key = keyname
		key.set_metadata('Content-Type', 'image/jpeg')
		key.set_contents_from_filename(filename, policy='public-read')
		
		os.remove(filename)
		
		return 'http://{bucket}.{host}/{key}'.format(host='s3.amazonaws.com', bucket=self.bucket.name, key=keyname)

	def consume(self):
		while True:
			try:
				job = self.beanstalk.reserve(timeout=300)
				if job is None:
					pc.logger.debug("Consumer timed out. Exiting")
					break
				else:
					series = pickle.loads(job.body)
					if series.cover_image_url is None:
						pc.logger.error("Dont have cover_image_url for %s - %s. Ignoring", series.name, series.url)
					else:
						series.cover_image = self.add_image(series.name, series.cover_image_url)
						db.persist(series)

						pc.logger.info("%s = %s", series.name, series.cover_image)

					job.delete()
			except Exception as e:
				pc.logger.error("Error: %s", e)

	def close(self):
		self.beanstalk.close()

def main():
	access_key, secret_key = pc.get_aws_config()
	conn = S3Connection(access_key, secret_key)
	bucket = conn.create_bucket("mangamadness_cover_images")

	host, port = pc.get_beanstalk_server()
	consumer = Consumer(host, port, bucket)
	consumer.consume()
	consumer.close()
	exit()

if __name__ == '__main__':
	main()