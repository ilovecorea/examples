package helper

import (
	"github.com/rs/zerolog"
	"github.com/rs/zerolog/log"
	"os"
)

func InitializeLogger() {
	// JSON 형식으로 로그를 출력하며, 타임스탬프를 포함하도록 설정
	log.Logger = zerolog.New(os.Stderr).With().Timestamp().Logger()
}
