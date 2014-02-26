#!/usr/bin/python
from models import MangaSeries
from models import MangaChapter
from models import MangaPage
from pymongo import MongoClient
from pymongo.son_manipulator import SONManipulator
from bson.binary import Binary
import pickle

sourceDb = MongoClient('localhost', 27017).manga_scrape
prodDb = MongoClient('localhost', 27017).production

def persist(object, theDb):
	collection = None
	if isinstance(object, MangaSeries):
		collection = theDb.series
	elif isinstance(object, MangaChapter):
		collection = theDb.chapters
	elif isinstance(object, MangaPage):
		collection = theDb.pages
	else:
		raise Exception('Unrecognised object: ' + object)

	record = {}
	if object._id is None:
		for (key, value) in object.__dict__.items():
			if key != '_id':
				record[key] = value
	else:
		record = object.__dict__
		
	return collection.save(record, manipulate=True)
