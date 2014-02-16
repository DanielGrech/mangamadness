package api

import (
	"github.com/codegangsta/martini"
	"github.com/martini-contrib/render"
	"labix.org/v2/mgo"
	"net/http"
	"net/url"
	"strconv"
	"mangamaster/common"
)

const defaultLimit = 30
const maxLimit = 100

func extract(values url.Values) (limit int, offset int, updatedSince int) {
	offset, _ = strconv.Atoi(values.Get("offset"))
	if offset < 0 {
		offset = 0
	}

	updatedSince, _ = strconv.Atoi(values.Get("updated_since"))

	// var err error;
	limit, err := strconv.Atoi(values.Get("limit"))
	if err != nil {
		limit = defaultLimit
	} else if limit < 0 {
		limit = 0
	} else if limit > maxLimit {
		limit = maxLimit
	}

	return
}

func GetSeriesList(request *http.Request, render render.Render, db *mgo.Database) {
	limit, offset, updated_since := extract(request.URL.Query())

	result := common.GetSeriesList(db, offset, limit, updated_since)
	render.JSON(http.StatusOK,  map[string]interface{} {
		"result" : result,
	})
} 

func GetSeries(request *http.Request, render render.Render, params martini.Params, db *mgo.Database) {
	series_id := params["id"]

	result := common.GetSeries(db, series_id)
	render.JSON(http.StatusOK,  map[string]interface{} {
		"result" : result,
	})
} 

func GetChapterList(request *http.Request, render render.Render, params martini.Params, db *mgo.Database) {
	series_id := params["id"]
	limit, offset, updated_since := extract(request.URL.Query())
	result := common.GetChapterList(db, series_id, offset, limit, updated_since)
	render.JSON(http.StatusOK,  map[string]interface{}{
		"result": result,
	})
} 

func GetChapter(params martini.Params, render render.Render, db *mgo.Database) {
	chapter_id := params["chapter_id"]

	result := common.GetPagesInChapter(db, chapter_id)
	render.JSON(http.StatusOK,  map[string]interface{} {
		"result" : result,
	})
} 
