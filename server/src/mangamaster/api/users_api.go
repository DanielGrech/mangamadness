package api

import (
	"github.com/codegangsta/martini"
	"github.com/martini-contrib/render"
	"labix.org/v2/mgo"
	"net/http"
	"mangamaster/common"
)

func Signup(request *http.Request, render render.Render, params martini.Params, db *mgo.Database) {
	render.JSON(http.StatusOK, map[string]interface{}{
		"Method" : "Signup",
	})
}

func Login(request *http.Request, render render.Render, params martini.Params, db *mgo.Database) {
	login_type := request.FormValue("type")
	username := request.FormValue("user")
	password := request.FormValue("password")
	access_token := request.FormValue("access_token")

	render.JSON(http.StatusOK, map[string]interface{}{
		"Method" : "Login",
		"type" : login_type,
		"username" : username,
		"password" : common.HashPassword(password),
		"access_token" : access_token,
	})
}
