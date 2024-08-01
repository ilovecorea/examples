package api

import (
	"encoding/json"
	"github.com/gin-gonic/gin"
	middleware "github.com/oapi-codegen/gin-middleware"
	"github.com/stretchr/testify/assert"
	"go-oapi-gin-petstore/config"
	"go-oapi-gin-petstore/helper"
	"net/http"
	"net/http/httptest"
	"strconv"
	"strings"
	"testing"
)

const testConfigPath = "../../postgres-petstore/docker-compose.yml"

func TestNewGinPetServer(t *testing.T) {
	// Docker Compose로 테스트용 데이터베이스 설정
	helper.StartComposeServices(&testing.T{}, testConfigPath)

	// 설정 초기화
	config.InitConfig()
	config.InitDB()

	// PetStore 인스턴스 생성
	petStore := NewPetStore()

	// Gin 서버 설정
	router := gin.Default()
	RegisterHandlers(router, petStore)

	// Swagger 설정 (필요시 추가)
	swagger, err := GetSwagger()
	if err != nil {
		t.Fatalf("Error loading swagger spec: %s", err)
	}
	router.Use(middleware.OapiRequestValidator(swagger))

	// 테스트 데이터 추가 (AddPet)
	body := `{"name": "Fluffy", "tag": "cat"}`
	req, err := http.NewRequest(http.MethodPost, "/pets", strings.NewReader(body))
	if err != nil {
		t.Fatalf("Could not create request: %v", err)
	}
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)
	assert.Equal(t, http.StatusCreated, w.Code)

	// 추가된 Pet ID 추출
	var addedPet Pet
	err = json.Unmarshal(w.Body.Bytes(), &addedPet)
	if err != nil {
		t.Fatalf("Could not unmarshal response: %v", err)
	}
	petID := addedPet.Id

	// Pet 조회 테스트 (FindPetByID)
	req, err = http.NewRequest(http.MethodGet, "/pets/"+strconv.FormatInt(petID, 10), nil)
	if err != nil {
		t.Fatalf("Could not create request: %v", err)
	}
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	assert.Equal(t, http.StatusOK, w.Code)

	// 삭제 테스트 (DeletePet)
	req, err = http.NewRequest(http.MethodDelete, "/pets/"+strconv.FormatInt(petID, 10), nil)
	if err != nil {
		t.Fatalf("Could not create request: %v", err)
	}
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	assert.Equal(t, http.StatusNoContent, w.Code)

	// 삭제 후 조회 시도 (FindPetByID)
	req, err = http.NewRequest(http.MethodGet, "/pets/"+strconv.FormatInt(petID, 10), nil)
	if err != nil {
		t.Fatalf("Could not create request: %v", err)
	}
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	assert.Equal(t, http.StatusNotFound, w.Code)
}
