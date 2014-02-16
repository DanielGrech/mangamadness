package common

import (
	"labix.org/v2/mgo"
	"labix.org/v2/mgo/bson"
	"time"
)

func GetSeriesList(db *mgo.Database, offset int, limit int, update_since int) (series []Series) {
	c := db.C("series")

	ts := bson.NewObjectIdWithTime(time.Unix(int64(update_since), 0))
	query := bson.M{"_id" : bson.M{"$gt" : ts} }
	c.Find(query).Limit(limit).Skip(offset).Sort("name").All(&series)
	return
}

func GetChapterList(db *mgo.Database, seriesId string, offset int, limit int, update_since int) (chapters []Chapter) {
	if bson.IsObjectIdHex(seriesId) {
		ts := bson.NewObjectIdWithTime(time.Unix(int64(update_since), 0))

		c := db.C("chapters")
		query := bson.M{
			"series_id" : bson.ObjectIdHex(seriesId),
			"_id"		: bson.M{"$gt" : ts},
		}
		c.Find(query).Limit(limit).Skip(offset).Sort("name").All(&chapters)
	}

	return
}

func GetSeries(db *mgo.Database, seriesId string) (series *Series) {
	if bson.IsObjectIdHex(seriesId) {
		c := db.C("series")
		query := bson.M{"_id" : bson.ObjectIdHex(seriesId)}
		c.Find(query).One(&series)
	} 

	return
}

func GetPagesInChapter(db *mgo.Database, chapterId string) (pages []Page) {
	if bson.IsObjectIdHex(chapterId) {
		c := db.C("pages")
		query := bson.M{"chapter_id" : bson.ObjectIdHex(chapterId)}
		c.Find(query).Sort("name").All(&pages)
	}

	return
}
