package httone

import (
	"appengine"
	"errors"
	"github.com/go-martini/martini"
	"github.com/martini-contrib/render"
	"net/http"
)

type Message struct {
	Body string `json:"message"`
}

func NotifyMessage(req *http.Request, params martini.Params, c appengine.Context, r render.Render, m Message) {

	recipientId := params["id"]
	senderId := req.Header.Get("X-Httone-Authentication")

	c.Infof("Recipient %s", recipientId)
	c.Infof("Sender %s", senderId)
	c.Infof("Message %s", m.Body)

	//TODO Validate them all

	err := PerformNotifyMessage(c, recipientId, senderId, m.Body)

	if err != nil {
		r.Error(400)
		c.Infof("Error sending message")
	}
}

func PerformNotifyMessage(c appengine.Context, recipient, sender, body string) error {

	if len(recipient) != 0 {

		var message map[string]interface{}
		message = make(map[string]interface{})

		message["sender"] = sender
		message["message"] = body

		regId, err := FindRegID(c, recipient)

		c.Infof("RegId %s", regId)
		c.Infof("Error %v", err)

		if err == nil && regId != "" {

			c.Infof("Sending message %s to user %s", body, recipient)

			SendPushNotification(c, message, regId)

			return nil
		}

		return err
	}

	return errors.New("Invalid recipient")
}
