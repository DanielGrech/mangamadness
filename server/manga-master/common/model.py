from google.appengine.ext import db

class Series(db.Model):
	id = db.StringProperty(required=True)
	name = db.StringProperty(required=True)
	artist = db.StringProperty()
	author = db.StringProperty()
	url_segment = db.StringProperty(required=True)
	summary = db.TextProperty()
	year_of_release = db.IntegerProperty()
	cover_image_url = db.LinkProperty()
	genres = db.StringListProperty()
	time_created = db.DateTimeProperty(auto_now_add=True)

class Chapter(db.Model):
	id = db.StringProperty(required=True)
	name = db.StringProperty(required=True)
	title = db.StringProperty() 
	series_id = db.StringProperty(required=True)
	sequence_number = db.IntegerProperty()
	time_created = db.DateTimeProperty(auto_now_add=True)
	release_date = db.DateProperty()

class Page(db.Model):
	id = db.StringProperty(required=True)
	chapter_id = db.StringProperty(required=True)
	sequence_number = db.IntegerProperty()
	image_url = db.LinkProperty()
	time_created = db.DateTimeProperty(auto_now_add=True)