#!/usr/bin/python

import collections
import json
import time
from collections import namedtuple

class MangaSeries():
	def __init__(self, name, url):
		self._id = None
		self.name = name
		self.url = url
		self.cover_image_url = None
		self.summary = None
		self.author = None
		self.artist = None
		self.year_of_release = None
		self.genres = []

	def __str__(self):
		return self.name.encode('ascii', 'ignore')

	def __repr__(self):
		return str(self)

	def add_genres(self, genres):
		if isinstance(genres, collections.Iterable):
			self.genres.extend(genres)
		else:
			self.genres.append(genres)

class MangaChapter():
	DATE_PATTERN = '%m/%d/%Y'

	def __init__(self, name, url, date_added):
		self._id = None
		self.series_id=None
		self.name = name
		self.url = url
		sequence_number = -1
		self.date_added = date_added

	def date_added_epoch(self):
		if self.date_added:
			return int(time.mktime(time.strptime(self.date_added, MangaChapter.DATE_PATTERN)))

	def __str__(self):
		return self.name.encode('ascii', 'ignore')

	def __repr__(self):
		return str(self)

class MangaPage():
	def __init__(self, name, url):
		self._id = None
		self.chapter_id = None
		self.series_id = None
		self.name = name
		self.url = url
		self.image_url = None
		self.image_id = None
		self.image = None


	def __str__(self):
		return self.name.encode('ascii', 'ignore')

	def __repr__(self):
		return str(self)