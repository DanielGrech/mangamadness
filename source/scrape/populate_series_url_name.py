#!/usr/bin/python

import ProducerConsumer as pc
from mangadb import db
from bson.objectid import ObjectId
from models import MangaPage
import logging
from datetime import datetime
import calendar
import re
import string

def main():
	series_cursor = db.series.find()
	for series_record in series_cursor:
		id = series_record.get('_id')
		series_name = series_record.get("name")

		series_name = series_name.lower()
		replacements = {
			' - '  : '-',
			' +'   : '',
			' & '  : '-',
			' -> ' : ' - ',
			' '    : '-'
		}

		for r in replacements:
			series_name = series_name.replace(r, replacements[r])
		
		subbed = re.sub("[a-z\\-_0-9]", "", series_name)
		if subbed:
			for i in range(0,len(subbed)):
				series_name = series_name.replace(subbed[i],"")

		series_record['url_segment'] = series_name
		db.series.save(series_record, manipulate=True)

if __name__ == '__main__':
	main()
