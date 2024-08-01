package api

import (
	"errors"
	"github.com/gin-gonic/gin"
	"go-oapi-gin-petstore/config"
	"go-oapi-gin-petstore/model"
	"gorm.io/gorm"
	"net/http"
)

// PetStore 구조체 정의
type PetStore struct {
	DB *gorm.DB
}

// NewPetStore 생성자 함수
func NewPetStore() *PetStore {
	return &PetStore{DB: config.DB}
}

// FindPets 메서드 구현
func (p PetStore) FindPets(c *gin.Context, params FindPetsParams) {
	var pets []Pet
	query := p.DB

	// 태그 필터링
	if params.Tags != nil && len(*params.Tags) > 0 {
		query = query.Where("tags IN (?)", params.Tags)
	}

	// 결과 제한
	if params.Limit != nil {
		query = query.Limit(int(*params.Limit))
	}

	if err := query.Find(&pets).Error; err != nil {
		c.JSON(http.StatusInternalServerError,
			Error{Code: http.StatusInternalServerError, Message: err.Error()})
		return
	}
	c.JSON(http.StatusOK, pets)
}

// AddPet 메서드 구현
func (p PetStore) AddPet(c *gin.Context) {
	var newPet NewPet
	if err := c.ShouldBind(&newPet); err != nil {
		c.JSON(http.StatusBadRequest, Error{Code: http.StatusBadRequest, Message: err.Error()})
		return
	}

	pet := model.Pet{
		Name: newPet.Name,
		Tag:  newPet.Tag,
	}

	if err := p.DB.Create(&pet).Error; err != nil {
		c.JSON(http.StatusInternalServerError, Error{Code: http.StatusInternalServerError, Message: err.Error()})
		return
	}
	c.JSON(http.StatusCreated, pet)
}

// DeletePet 메서드 구현
func (p PetStore) DeletePet(c *gin.Context, id int64) {
	if err := p.DB.Delete(&Pet{}, id).Error; err != nil {
		c.JSON(http.StatusInternalServerError, Error{Code: http.StatusInternalServerError, Message: err.Error()})
		return
	}
	c.Status(http.StatusNoContent)
}

// FindPetByID 메서드 구현
func (p PetStore) FindPetByID(c *gin.Context, id int64) {
	var pet Pet
	if err := p.DB.First(&pet, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			c.JSON(http.StatusNotFound, Error{Code: http.StatusNotFound, Message: err.Error()})
		} else {
			c.JSON(http.StatusInternalServerError, Error{Code: http.StatusInternalServerError, Message: err.Error()})
		}
		return
	}
	c.JSON(http.StatusOK, pet)
}

var _ ServerInterface = (*PetStore)(nil)
