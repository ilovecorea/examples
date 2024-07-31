package main

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"go-oapi-gin-petstore/api"
	"go-oapi-gin-petstore/config"
	"net"
	"net/http"
	"os"

	middleware "github.com/oapi-codegen/gin-middleware"
)

func NewGinPetServer(petStore *api.PetStore, port string) *http.Server {
	swagger, err := api.GetSwagger()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error loading swagger spec\n: %s", err)
		os.Exit(1)
	}

	swagger.Servers = nil
	r := gin.Default()
	r.Use(middleware.OapiRequestValidator(swagger))
	api.RegisterHandlers(r, petStore)

	s := &http.Server{
		Handler: r,
		Addr:    net.JoinHostPort("0.0.0.0", port),
	}
	return s
}

func main() {
	config.InitConfig()
}
