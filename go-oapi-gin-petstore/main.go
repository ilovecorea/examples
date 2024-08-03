package main

import (
	"errors"
	"fmt"
	"github.com/gin-gonic/gin"
	"go-oapi-gin-petstore/api"
	"go-oapi-gin-petstore/config"
	"go-oapi-gin-petstore/middleware"
	"net"
	"net/http"
	"os"

	gin_middleware "github.com/oapi-codegen/gin-middleware"
)

func NewGinPetServer(petStore *api.PetStore, port string) *http.Server {
	swagger, err := api.GetSwagger()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error loading swagger spec\n: %s", err)
		os.Exit(1)
	}

	swagger.Servers = nil
	r := gin.Default()
	r.Use(gin_middleware.OapiRequestValidator(swagger))
	// Add jwt middleware
	r.Use(middleware.AuthMiddleware)
	// Add logging middleware
	r.Use(middleware.RequestLoggerMiddleware)
	api.RegisterHandlers(r, petStore)

	s := &http.Server{
		Handler: r,
		Addr:    net.JoinHostPort("0.0.0.0", port),
	}
	return s
}

func main() {
	// 설정 초기화
	config.InitConfig()

	// 데이터베이스 초기화
	config.InitDB()

	// PetStore 인스턴스 생성
	petStore := api.NewPetStore()

	// Gin 서버 생성
	server := NewGinPetServer(petStore, "8080")

	// 서버 시작
	if err := server.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
		fmt.Fprintf(os.Stderr, "Server error: %s\n", err)
		os.Exit(1)
	}
}
