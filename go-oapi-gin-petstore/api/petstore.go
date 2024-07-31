package api

import (
	"github.com/gin-gonic/gin"
)

type PetStore struct {
}

func (p PetStore) FindPets(c *gin.Context, params FindPetsParams) {
	//TODO implement me
	panic("implement me")
}

func (p PetStore) AddPet(c *gin.Context) {
	//TODO implement me
	panic("implement me")
}

func (p PetStore) DeletePet(c *gin.Context, id int64) {
	//TODO implement me
	panic("implement me")
}

func (p PetStore) FindPetByID(c *gin.Context, id int64) {
	//TODO implement me
	panic("implement me")
}

var _ ServerInterface = (*PetStore)(nil)
