package main

import (
	"github.com/codegangsta/martini"
	"github.com/martini-contrib/render"
	"net/http"
  "mangamaster/api"
)

func notFound(res http.ResponseWriter, req *http.Request) (int, string) {
  	return http.StatusNotFound, "Oh Dear!"
 }

func main() {
  m := martini.Classic()

  m.Use(render.Renderer())
  m.Get("/api/series", api.GetSeriesList)
  m.Get("/api/series/:id", api.GetSeries)
  m.Get("/api/chapters", api.GetChapterList)
  m.Get("/api/chapters/:chapter_id", api.GetChapter)
  m.NotFound(notFound)

  http.ListenAndServe(":8080", m)
}

