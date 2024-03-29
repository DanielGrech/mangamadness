from flask import Flask, got_request_exception
from flask.ext.restful import Api, Resource, reqparse
from flask.ext.compress import Compress
import logging
# from common import api

DEFAULT_LIMIT = 100
MAX_LIMIT = 250

parser = reqparse.RequestParser()
parser.add_argument('limit', type=int, help="limit cannot be converted")
parser.add_argument('offset', type=int, help="offset cannot be converted")
parser.add_argument('updated_since', type=int, help="updated_since cannot be converted")

def extract(args):
	limit = args["limit"]
	offset = args["offset"]
	updated_since = args["updated_since"]

	offset = 0 if offset is None or offset < 0 else offset
	updated_since = 0 if updated_since is None or updated_since < 0 else updated_since

	if limit is None or limit <= 0:
		limit = DEFAULT_LIMIT
	elif limit > MAX_LIMIT:
		limit = DEFAULT_LIMIT

	return limit, offset, updated_since


class SeriesListRequest(Resource):
	def get(self):
		limit, offset, updated_since = extract(parser.parse_args())

		response = {
			"limit" : limit,
			"offset" : offset,
			"updated_since" : updated_since,
		}

		result = None# api.get_series_list(limit, offset, updated_since)
		response["result"] = [] if result is None else result
		return response

class SeriesRequest(Resource):
	def get(self, series_name):
		return {
			"result" : None #api.get_series(series_name)
		}
		

class SearchSeriesRequest(Resource):
	def get(self, query):
		limit, offset, updated_since = extract(parser.parse_args())

		response = {
			"limit" : limit,
			"offset" : offset,
			"updated_since" : updated_since,
			"query" : query
		}

		result = None #api.search_series(query, limit, offset, updated_since)
		response["result"] = [] if result is None else result
		return response

class ChaptersBySeriesRequest(Resource):
	def get(self, series_name):
		limit, offset, updated_since = extract(parser.parse_args())

		response = {
			"limit" : limit,
			"offset" : offset,
			"updated_since" : updated_since,
		}

		result = None #api.get_chapter_list(series_name, limit, offset, updated_since)
		response["result"] = [] if result is None else result
		return response

class ChapterBySeriesAndNumberRequest(Resource):
	def get(self, series_name, chapter_number):
		return {
			"series" : series_name,
			"chapter" : chapter_number,
			"result" : None #api.get_chapter(series_name, chapter_number)
		}

class ChapterRequest(Resource):
	def get(self, chapter_id):
		return {
			"result" : None #api.get_chapter_by_id(chapter_id)
		}

app = Flask(__name__)

def log_exception(sender, exception, **extra):
    app.logger.exception(exception)

def setup_logger(app):
	got_request_exception.connect(log_exception, app)

def setup_routes(app):	
	Compress(app)
	
	api = Api(app)
	api.add_resource(SeriesListRequest, '/series')
	api.add_resource(SearchSeriesRequest, '/series/search/<string:query>')	
	api.add_resource(SeriesRequest, '/series/<string:series_name>')
	api.add_resource(ChaptersBySeriesRequest, '/series/<string:series_name>/chapters')
	api.add_resource(ChapterBySeriesAndNumberRequest, '/series/<string:series_name>/chapters/<int:chapter_number>')
	api.add_resource(ChapterRequest, '/chapters/<string:chapter_id>')

setup_routes(app)
setup_logger(app)