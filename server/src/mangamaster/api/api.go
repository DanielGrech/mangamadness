package api

import (
	"github.com/codegangsta/martini"
	"github.com/martini-contrib/render"
	"labix.org/v2/mgo"
	"net/http"
	"net/url"
	"strconv"
)

const defaultLimit = 30

func extract(values url.Values) (limit int, offset int, updatedSince int) {
	offset, _ = strconv.Atoi(values.Get("offset"))
	updatedSince, _ = strconv.Atoi(values.Get("updated_since"))

	// var err error;
	limit, err := strconv.Atoi(values.Get("limit"))
	if err != nil {
		limit = defaultLimit
	}

	return
}

func GetSeriesList(request *http.Request, render render.Render, db *mgo.Database) {
	qs := request.URL.Query()

	limit, offset, updated_since := extract(qs)

	var result [] struct{ Name string; Url string }
	c := db.C("series")
	c.Find(nil).Limit(limit).Skip(offset).Sort("name").All(&result)

	render.JSON(http.StatusOK,  map[string]interface{} {
		"func" : "GetSeriesList", 
		"limit" : limit,
		"offset" : offset,
		"updated_since" : updated_since,
		"result" : result,
	})
} 

func GetSeries(request *http.Request, render render.Render, params martini.Params) {
	render.JSON(http.StatusOK,  map[string]interface{}{"func": "GetSeries:" + params["id"]})
} 

func GetChapterList(request *http.Request, render render.Render, params martini.Params) {
	render.JSON(http.StatusOK,  map[string]interface{}{"func": "GetChapterList"})
} 

func GetChapter(request *http.Request, render render.Render, params martini.Params) {
	render.JSON(http.StatusOK,  map[string]interface{}{"func": "GetChapter:" + params["chapter_id"]})
} 
