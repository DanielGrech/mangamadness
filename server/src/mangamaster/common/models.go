package common

import (
	"labix.org/v2/mgo/bson"
)

type Page struct {
	Id bson.ObjectId `bson:"_id" json:"id"`
	Chapter_Id bson.ObjectId `json:"chapter_id"` 
	Image_url string `json:"image_url"` 
	Sequence_number int `json:"name"` 
	Time_created int64 `json:"time_created"` 
}

type Chapter struct {
	Id bson.ObjectId `bson:"_id" json:"id"`
	Series_Id bson.ObjectId `json:"series_id"` 	
	Name string `json:"name"`
	Title string `json:"title"`
	Sequence_number int `json:"sequence_number"`
	Release_date int64 `json:"release_date"`
	Time_created int64 `json:"time_created"` 
}

type Series struct {
	Id bson.ObjectId `bson:"_id" json:"id"`
	Name string `json:"name"` 
	Author string `json:"author"` 
	Artist string `json:"artist"` 
	Summary string `json:"summary"`
	Url_segment string `json:"url_segment"`
	Cover_image_url string `json:"cover_image_url"`
	Year_of_release int `json:"year_of_release"`
	Genres []string `json:"genres"` 
	Time_created int64 `json:"time_created"`  
}