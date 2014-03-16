from flask import Flask, jsonify
from flask.ext.restful import Api, Resource, reqparse
from common import api

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

		result = api.get_series_list(limit, offset, updated_since)
		response["result"] = [] if result is None else result
		return response

class SeriesRequest(Resource):
	def get(self, series_name):
		return {
			"result" : api.get_series(series_name)
		}
		

class SearchSeriesRequest(Resource):
	def get(self, query):
		return {"result" : "Hello, " + str(query)}

class ChaptersBySeriesRequest(Resource):
	def get(self, series_name):
		limit, offset, updated_since = extract(parser.parse_args())

		response = {
			"limit" : limit,
			"offset" : offset,
			"updated_since" : updated_since,
		}

		result = api.get_chapter_list(series_name, limit, offset, updated_since)
		response["result"] = [] if result is None else result
		return response

class ChapterBySeriesAndNumberRequest(Resource):
	def get(self, series_name, chapter_number):
		return {"result" : "Hello, " + str(series_name) + " chapter " + str(chapter_number)}

class ChapterRequest(Resource):
	def get(self, chapter_id):
		return {"result" : "Hello, " + str(chapter_id)}

def main():
	app = Flask(__name__)
	api = Api(app)
	
	api.add_resource(SeriesListRequest, '/series')
	api.add_resource(SearchSeriesRequest, '/series/search/<string:query>')	
	api.add_resource(SeriesRequest, '/series/<string:series_name>')
	api.add_resource(ChaptersBySeriesRequest, '/series/<string:series_name>/chapters')
	api.add_resource(ChapterBySeriesAndNumberRequest, '/series/<string:series_name>/chapters/<int:chapter_number>')
	api.add_resource(ChapterRequest, '/chapters/<string:chapter_id>')

	app.run(debug=True)

if __name__ == '__main__':
	main()