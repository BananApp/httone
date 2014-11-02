package httone

import (
	"appengine"
	"appengine/datastore"
	"fmt"
	"github.com/martini-contrib/render"
)

type User struct {
	Email          string `json:"email"`
	RegistrationID string `json:"registration_id"`
}

func (u *User) String() string {
	return fmt.Sprintf("%s - %s", u.Email, u.RegistrationID)
}

func CreateUser(c appengine.Context, r render.Render, user User) {
	q := datastore.NewQuery("user").Filter("Email =", user.Email)

	var users []User
	keys, _ := q.GetAll(c, &users)

	if len(keys) == 0 {

		//Insert a new user
		key := datastore.NewIncompleteKey(c, "user", nil)
		_, err := datastore.Put(c, key, &user)

		if err != nil {
			r.Error(500)
			return
		}

	} else {

		//Update the already registered user
		_, err := datastore.Put(c, keys[0], &user)

		if err != nil {
			r.Error(500)
			return
		}
	}
}

func ListUserStatus(c appengine.Context, r render.Render) {
	q := datastore.NewQuery("user_location")

	var userLocation []UserLocation
	_, err := q.GetAll(c, &userLocation)

	if err != nil {
		r.Error(500)
	}

	if userLocation == nil {
		userLocation = make([]UserLocation, 0)
	}

	r.JSON(200, userLocation)
}
