package httone

import (
	"appengine"
	"appengine/datastore"
	"errors"
	"fmt"
	"github.com/go-martini/martini"
	"github.com/martini-contrib/render"
	"net/http"
	"strconv"
)

type Place struct {
	Id        int64   `json:"id"`
	Name      string  `json:"name"`
	Latitude  float64 `json:"latitude"`
	Longitude float64 `json:"longitude"`
	Radius    float64 `json:"radius"`
}

type UserLocation struct {
	UserId        string `json:"user_id"`
	CurrentPlace  Place  `json:"current_place"`
	PreviousPlace Place  `json:"previous_place"`
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

	if places == nil {
		places = make([]Place, 0)
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

func EnterPlace(req *http.Request, params martini.Params, c appengine.Context, r render.Render) {

	id := params["id"]
	user := req.Header.Get("X-Httone-Authentication")

	if len(user) != 0 {

		//TODO valide place exists

		err := RegisterPlaceEnter(c, id, user)

		if err != nil {
			r.Error(400)

			return
		}

		NotifyUsers(c)

	} else {

		r.Error(400)
	}

	c.Infof("Register place %d enter for user %s", id, user)
}

func RegisterPlaceEnter(c appengine.Context, placeId string, userId string) error {

	//TODO support previous placeId

	place, err := PlaceById(c, placeId)

	if err != nil {

		return err
	}

	l := UserLocation{
		UserId:       userId,
		CurrentPlace: place,
	}

	datastore.Put(c, datastore.NewIncompleteKey(c, "user_location", nil), &l)

	return nil
}

func NotifyUsers(c appengine.Context) {

	m := make(map[string]interface{})
	var regIds []string

	q := datastore.NewQuery("user").
		Project("RegistrationID")

	t := q.Run(c)

	for {
		var u User
		_, err := t.Next(&u)

		if err == datastore.Done {
			break
		}

		regIds = append(regIds, u.RegistrationID)
	}

	if len(regIds) > 0 {

		SendPushNotification(c, m, regIds)

	} else {

		c.Infof("No registration ID to send the notication")
	}
}

func PlaceById(c appengine.Context, id string) (Place, error) {

	placeId, _ := strconv.ParseInt(id, 0, 64)

	k := datastore.NewKey(c, "place", "", placeId, nil)
	q := datastore.NewQuery("place").Filter("__key__ =", k)

	var places []Place
	keys, err := q.GetAll(c, &places)

	if err != nil || len(places) != 1 {

		return Place{}, errors.New("no place found")
	}

	places[0].SetId(keys[0].IntID())

	return places[0], nil
}
