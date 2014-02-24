package common

import (
	"labix.org/v2/mgo/bson"
)

type Page struct {
	Id bson.ObjectId "_id"
	Chapter_Id bson.ObjectId `json:"chapter_id"` 
	Name int `json:"name"` 
	Url string `json:"original_image_url"` 
}

type Chapter struct {
	Id bson.ObjectId "_id"
	Series_Id bson.ObjectId `json:"series_id"` 	
	Name string `json:"name"`
	Sequence_number int `json:"sequence_number"`
	Date_added string `json:"date_added"`
}

type Series struct {
	Id bson.ObjectId `bson:"_id", json:"id"`
	Name string `json:"name"` 
	Url_segment string `json:"url_segment"`
	Author string `json:"author"` 
	Artist string `json:"artist"` 
	Genres []string `json:"genres"` 
	Summary string `json:"summary"` 
}