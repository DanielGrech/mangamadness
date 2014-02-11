#!/usr/bin/python
from models import MangaSeries
from models import MangaChapter
from models import MangaPage
from pymongo import MongoClient
from pymongo.son_manipulator import SONManipulator
import gridfs
from bson.binary import Binary
import pickle

class TransformToBinary(SONManipulator):
	def needs_custom_transform(self, object):
		return isinstance(object, MangaSeries) or isinstance(object, MangaChapter) or isinstance(object, MangaPage)

	def transform_incoming(self, son, collection):
		print 'transform_incoming'
		for (key, value) in son.items():
			print 'Incoming', key, '=',value
			if self.needs_custom_transform(value):
				son[key] = Binary(value, 128)
			elif isinstance(value, dict):
				son[key] = self.transform_incoming(value, collection)
		print "Returning ", son
		return son

	def transform_outgoing(self, son, collection):
		print "Transforming_outgoing!"
		for (key, value) in son.items():
			if isinstance(value, Binary) and value.subtype == 128:
				print "Transforming outgoing!"
				son[key] = value
			elif isinstance(value, dict):
				son[key] = self.transform_outgoing(value, collection)
		return son

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

	print "Persisting", object
	return collection.save(object.__dict__, manipulate=True)

def persist_raw(data, filename, mimetype):
	return gridFs.put(data, contentType=mimetype, filename=filename)
