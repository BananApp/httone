package httone

import (
	"appengine"
	"github.com/go-martini/martini"
	"github.com/martini-contrib/binding"
	"github.com/martini-contrib/render"
	"net/http"
)

func init() {

	m := martini.Classic()

	m.Use(AppEngine)
	m.Use(martini.Logger())
	m.Use(render.Renderer())

	m.Group("/places", func(r martini.Router) {
		r.Post("/", binding.Bind(Place{}), NewPlace)
		r.Get("/", ListPlaces)
	})

	m.Group("/users", func(r martini.Router) {
		r.Post("/", binding.Bind(User{}), CreateUser)
		r.Get("/", ListPlaces)
	})

	http.Handle("/", m)
}

func AppEngine(c martini.Context, r *http.Request) {
	c.MapTo(appengine.NewContext(r), (*appengine.Context)(nil))
}
