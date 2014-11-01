package httone

import (
	"fmt"
)

type Place struct {
	Id        string  `json:"id"`
	Name      string  `json:"name"`
	Latitude  float64 `json:"latitude"`
	Longitude float64 `json:"longitude"`
	Radius    float64 `json:"radius"`
}

func (p *Place) SetId(id string) {
	p.Id = id
}

func (p *Place) String() string {
	return fmt.Sprintf("%s - %s (%f, %f)", p.Id, p.Name, p.Latitude, p.Longitude)
}
