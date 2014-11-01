package httone

import (
	"appengine"
	"appengine/datastore"
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

	m.Group("/place", func(r martini.Router) {
		r.Post("/", binding.Bind(Place{}), NewPlace)
	})

	http.Handle("/", m)
}

func NewPlace(c appengine.Context, r render.Render, place Place) {

	key := datastore.NewIncompleteKey(c, "place", nil)

	place.SetId(key.Encode())

	key, err := datastore.Put(c, key, &place)

	if err != nil {
		r.Error(500)
	}

	r.JSON(200, place)
}

func AppEngine(c martini.Context, r *http.Request) {
	c.MapTo(appengine.NewContext(r), (*appengine.Context)(nil))
}
