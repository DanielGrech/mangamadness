package common

import (
	"labix.org/v2/mgo"
	"labix.org/v2/mgo/bson"
	"time"
	"strings"
	"regexp"
)

var charactersRegex = regexp.MustCompile(`[A-Za-z0-9]`)

func stripchars(str, chr string) string {
    return strings.Map(func(r rune) rune {
        if strings.IndexRune(chr, r) < 0 {
            return r
        }
        return -1
    }, str)
}

func SearchSeries(db *mgo.Database, query string, limit int, update_since int) (series []Series) {
	query = stripchars(query, "*/")
	if charactersRegex.MatchString(query) {
		c := db.C("series")
		ts := bson.NewObjectIdWithTime(time.Unix(int64(update_since), 0))
		q := bson.M{
			"_id" : bson.M{
				"$gt" : ts,
			},
			"name" : bson.M {
				"$regex" : ".*" + query + ".*",
				"$options" : "-i",
			},
		}

		c.Find(q).Limit(limit).Sort("name").All(&series)
	}

	return
}

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
		c.Find(query).Limit(limit).Skip(offset).Sort("sequence_number").All(&chapters)
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
