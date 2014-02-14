package api

import (
	"github.com/codegangsta/martini"
	"github.com/martini-contrib/render"
	"net/http"
	"strconv"
	"fmt"
)

func extractInt(values *Values, key *string, defaultValue int) (int) {
	return 0
}

func GetSeriesList(request *http.Request, render render.Render, params martini.Params) {
	qs := request.URL.Query()

	limit, err := strconv.Atoi(qs.Get("limit"))
	if err != nil {
		fmt.Println("Passed invalid 'limit' - ignoring: " + err.Error())
		limit = 30 //TODO: Constant for default
	}


	offset , err := strconv.Atoi(qs.Get("offset"))
	if err != nil {
		fmt.Println("Passed invalid 'offset' - ignoring: " + err.Error())
	}

	updated_since , err := strconv.Atoi(qs.Get("updated_since"))
	if err != nil {
		fmt.Println("Passed invalid 'updated_since' - ignoring: " + err.Error())
	}

	render.JSON(http.StatusOK,  map[string]interface{} {
		"func" : "GetSeriesList", 
		"limit" : limit,
		"offset" : offset,
		"updated_since" : updated_since,
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
