package httone

import (
	"appengine"
	"appengine/urlfetch"
	"github.com/alexjlockwood/gcm"
)

func SendPushNotification(c appengine.Context, data map[string]interface{}, regIDs []string) error {
	client := urlfetch.Client(c)
	c.Infof("Sending push to: %v", regIDs[0])

	msg := gcm.NewMessage(data, regIDs...)

	// Create a Sender to send the message.
	sender := &gcm.Sender{ApiKey: "api-key", Http: client}

	// Send the message and receive the response after at most two retries.
	response, err := sender.Send(msg, 2)

	c.Infof("Error: %v", err)
	c.Infof("Result: %v", response)

	return err
}
