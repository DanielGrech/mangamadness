package main

import (
	"github.com/codegangsta/martini"
	"github.com/martini-contrib/render"
  "labix.org/v2/mgo"
  // "labix.org/v2/mgo/bson"
  "mangamaster/api"
	"net/http"
)

func notFound(res http.ResponseWriter, req *http.Request) (int, string) {
  	return http.StatusNotFound, "Oh Dear!"
 }

func main() {
  session, err := mgo.Dial("mongodb://127.0.0.1:27017")
  if err != nil {
    panic(err)
  }
  defer session.Close()

  m := martini.Classic()

  m.Map(session.DB("manga_scrape"))
  m.Use(render.Renderer())
  m.Get("/api/series", api.GetSeriesList)
  m.Get("/api/series/search/:query", api.GetSeriesList)
  m.Get("/api/series/:id", api.GetSeries)
  m.Get("/api/series/:id/chapters", api.GetChapterList)
  m.Get("/api/chapters/:chapter_id", api.GetChapter)
  m.NotFound(notFound)

  http.ListenAndServe(":8080", m)
}

