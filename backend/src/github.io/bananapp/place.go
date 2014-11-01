package httone

import (
	"appengine"
	"appengine/datastore"
	"fmt"
	"github.com/martini-contrib/render"
)

type Place struct {
	Id        int64   `json:"id"`
	Name      string  `json:"name"`
	Latitude  float64 `json:"latitude"`
	Longitude float64 `json:"longitude"`
	Radius    float64 `json:"radius"`
}

func (p *Place) SetId(id int64) {
	p.Id = id
}

func (p *Place) String() string {
	return fmt.Sprintf("%s - %s (%f, %f)", p.Id, p.Name, p.Latitude, p.Longitude)
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
