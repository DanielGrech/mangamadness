package api

import (
	"github.com/codegangsta/martini"
	"github.com/martini-contrib/render"
	"labix.org/v2/mgo"
	"labix.org/v2/mgo/bson"
	"net/http"
	"net/url"
	"strconv"
	"mangamaster/common"
)

const defaultLimit = 30
const maxLimit = 500

var EMPTY_JSON_OBJECT = bson.M{};
var EMPTY_JSON_ARRAY []string = []string{};

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

func SearchSeries(request *http.Request, render render.Render, params martini.Params, db *mgo.Database) {
	limit, _, updated_since := extract(request.URL.Query())
	query := params["query"]
	result := common.SearchSeries(db, query, limit, updated_since)
	if result == nil {
		render.JSON(http.StatusOK, EMPTY_JSON_ARRAY)
	} else {
		render.JSON(http.StatusOK, result)
	}
}

func GetSeriesList(request *http.Request, render render.Render, db *mgo.Database) {
	limit, offset, updated_since := extract(request.URL.Query())

	result := common.GetSeriesList(db, offset, limit, updated_since)
	if result == nil {
		render.JSON(http.StatusOK, EMPTY_JSON_ARRAY)
	} else {
		render.JSON(http.StatusOK, result)
	}
} 

func GetSeries(request *http.Request, render render.Render, params martini.Params, db *mgo.Database) {
	series_name := params["series_name"]

	result := common.GetSeries(db, series_name)
	if result == nil {
		render.JSON(http.StatusOK, EMPTY_JSON_OBJECT)
	} else {
		render.JSON(http.StatusOK, result)
	}
} 

func GetChapterList(request *http.Request, render render.Render, params martini.Params, db *mgo.Database) {
	series_name := params["series_name"]
	limit, offset, updated_since := extract(request.URL.Query())
	result := common.GetChapterList(db, series_name, offset, limit, updated_since)
	if result == nil {
		render.JSON(http.StatusOK, EMPTY_JSON_ARRAY)
	} else {
		render.JSON(http.StatusOK, result)
	}
} 

func GetChapterFromSeries(render render.Render, params martini.Params, db *mgo.Database) {
	series_name := params["series_name"]
	chapter_num_str := params["chapter_number"]	
	chapter_num, error := strconv.Atoi(chapter_num_str)
	if error != nil {
		render.JSON(http.StatusOK, EMPTY_JSON_ARRAY)
	} else {
		result := common.GetChapterFromSeries(db, series_name, chapter_num)
		if result == nil {
			render.JSON(http.StatusOK, EMPTY_JSON_ARRAY)
		} else {
			render.JSON(http.StatusOK, result)
		}
	}
}

func GetChapter(params martini.Params, render render.Render, db *mgo.Database) {
	chapter_id := params["chapter_id"]

	result := common.GetPagesInChapter(db, chapter_id)
	if result == nil {
		render.JSON(http.StatusOK, EMPTY_JSON_ARRAY)
	} else {
		render.JSON(http.StatusOK, result)
	}
} 
