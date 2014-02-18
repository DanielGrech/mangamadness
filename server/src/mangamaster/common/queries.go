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

func getSeries(db *mgo.Database, url_segment string) (series *Series) {
	c := db.C("series")
	query := bson.M{"url_segment" : url_segment}
	c.Find(query).One(&series)	
	return
}

func getChapterId(db *mgo.Database, series_id bson.ObjectId, sequence_number int) (chapter_id bson.ObjectId) {
	c := db.C("chapters")
	query := bson.M{"series_id" : series_id, "sequence_number" : sequence_number}

	var chapter *Chapter;
	c.Find(query).One(&chapter)
	if chapter != nil {
		chapter_id = chapter.Id
	}

	return
}

func getPagesInChapter(db *mgo.Database, chapter_id bson.ObjectId) (pages []Page) {
	c := db.C("pages")
	query := bson.M{"chapter_id" : chapter_id}
	c.Find(query).Sort("name").All(&pages)
	return
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

func GetChapterList(db *mgo.Database, series_name string, offset int, limit int, update_since int) (chapters []Chapter) {
	series := getSeries(db, series_name)
	if series != nil {
		ts := bson.NewObjectIdWithTime(time.Unix(int64(update_since), 0))

		c := db.C("chapters")
		query := bson.M{
			"series_id" : series.Id,
			"_id"		: bson.M{"$gt" : ts},
		}
		c.Find(query).Limit(limit).Skip(offset).Sort("sequence_number").All(&chapters)
	}

	return
}

func GetSeries(db *mgo.Database, url_segment string) (series *Series) {
	series = getSeries(db, url_segment)
	return
}

func GetChapterFromSeries(db *mgo.Database, series_url_segment string, chapter_number int) (pages []Page) {
	series := getSeries(db, series_url_segment)
	if series != nil {
		pages = getPagesInChapter(db, getChapterId(db, series.Id, chapter_number))
	}
	return
}

func GetPagesInChapter(db *mgo.Database, chapter_id string) (pages []Page) {
	if bson.IsObjectIdHex(chapter_id) {
		pages = getPagesInChapter(db, bson.ObjectIdHex(chapter_id))
	}

	return
}
