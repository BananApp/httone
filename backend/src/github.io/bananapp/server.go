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

	m.Get("/", func() string {
		return "Nothing to see here"
	})

	m.Group("/places", func(r martini.Router) {
		r.Post("/", binding.Bind(Place{}), NewPlace)
		r.Get("/", ListPlaces)
		r.Put("/:id", EnterPlace)
	})

	m.Group("/users", func(r martini.Router) {
		r.Get("/status", ListUserStatus)
		r.Post("/", binding.Bind(User{}), CreateUser)
		r.Post("/notify/:id", binding.Bind(Message{}), NotifyMessage)
	})

	m.Group("/push", func(r martini.Router) {
		r.Get("/test", NotifyUsers)
	})

	http.Handle("/", m)
}

func AppEngine(c martini.Context, r *http.Request) {
	c.MapTo(appengine.NewContext(r), (*appengine.Context)(nil))
}
