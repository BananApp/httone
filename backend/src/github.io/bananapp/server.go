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

	m.Group("/places", func(r martini.Router) {
		r.Post("/", binding.Bind(Place{}), NewPlace)
		r.Get("/", ListPlaces)
	})

	http.Handle("/", m)
}

func ListPlaces(c appengine.Context, r render.Render) {

	q := datastore.NewQuery("place")

	var places []Place
	keys, err := q.GetAll(c, &places)

	for index, element := range keys {
		places[index].SetId(element.IntID())
	}

	if err != nil {
		r.Error(500)
	}

	r.JSON(200, places)
}

func NewPlace(c appengine.Context, r render.Render, place Place) {

	key := datastore.NewIncompleteKey(c, "place", nil)

	key, err := datastore.Put(c, key, &place)

	if err != nil {
		r.Error(500)
	}

	place.SetId(key.IntID())

	r.JSON(200, place)
}

func AppEngine(c martini.Context, r *http.Request) {
	c.MapTo(appengine.NewContext(r), (*appengine.Context)(nil))
}
