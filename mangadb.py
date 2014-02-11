#!/usr/bin/python
from models import MangaSeries
from models import MangaChapter
from models import MangaPage
from pymongo import MongoClient
from pymongo.son_manipulator import SONManipulator
import gridfs
from bson.binary import Binary
import pickle

db = MongoClient('localhost', 27017).manga_scrape
# db.add_son_manipulator(TransformToBinary())
gridFs = gridfs.GridFS(db)

def persist(object):
	collection = None
	if isinstance(object, MangaSeries):
		collection = db.series
	elif isinstance(object, MangaChapter):
		collection = db.chapters
	elif isinstance(object, MangaPage):
		collection = db.pages
	else:
		raise Exception('Unrecognised object: ' + object)

	record = {}
	if object._id is None:
		for (key, value) in object.__dict__.items():
			if key != '_id':
				record[key] = value
		
	return collection.save(record, manipulate=True)
		

def persist_raw(data, filename, mimetype):
	return gridFs.put(data, contentType=mimetype, filename=filename)
