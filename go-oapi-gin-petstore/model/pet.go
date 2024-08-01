package model

type Pet struct {
	ID   int64   `gorm:"primary_key;auto_increment" json:"id"`
	Name string  `gorm:"size:255;not null;unique" json:"name"`
	Tag  *string `gorm:"size:255" json:"tag,omitempty"`
}
