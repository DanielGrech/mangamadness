package main

import (
	"github.com/codegangsta/martini"
  "github.com/martini-contrib/gzip"
	"github.com/martini-contrib/render"
  "labix.org/v2/mgo"
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

  m.Map(session.DB("production"))
  m.Use(gzip.All())
  m.Use(render.Renderer())

  // Manga

  m.Get("/api/series", api.GetSeriesList)
  m.Get("/api/series/search/:query", api.SearchSeries)
  m.Get("/api/series/:series_name", api.GetSeries)
  m.Get("/api/series/:series_name/chapters", api.GetChapterList)
  m.Get("/api/series/:series_name/chapters/:chapter_number", api.GetChapterFromSeries)
  m.Get("/api/chapters/:chapter_id", api.GetChapter)

  // Users
  m.Post("/api/users/signup", api.Signup)
  m.Post("/api/users/login", api.Login)

  m.NotFound(notFound)

  http.ListenAndServe("0.0.0.0:8080", m)
}
