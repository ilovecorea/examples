package middleware

import (
	"log"

	"github.com/gin-gonic/gin"
)

func RequestLoggerMiddleware(c *gin.Context) {
	// 로그 메시지를 생성
	log.Printf("Request URL: %s", c.Request.URL.String())

	// 다음 핸들러로 요청을 전달
	c.Next()
}
