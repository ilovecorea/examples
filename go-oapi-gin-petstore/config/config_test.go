package config

import (
	"github.com/rs/zerolog/log"
	"github.com/spf13/viper"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"go-oapi-gin-petstore/helper"
	"os"
	"testing"
)

func TestInitConfig(t *testing.T) {
	helper.InitializeLogger()
	// 환경 변수를 설정합니다.
	os.Setenv("GO_PROFILE", "local")
	defer os.Unsetenv("GO_PROFILE")

	// 설정 파일이 존재하는지 확인하기 위한 가상 파일 시스템을 사용합니다.
	viper.AddConfigPath("../config")
	viper.SetConfigName("cfg.local")
	viper.SetConfigType("yaml")

	// 초기화 함수를 호출합니다.
	InitConfig()

	// 설정 값을 테스트합니다.
	expectedProfile := "local"
	actualProfile := viper.GetString("GO_PROFILE")
	log.Debug().
		Str("actualProfile", actualProfile).
		Msg("Config")
	assert.Equal(t, expectedProfile, actualProfile, "프로파일 이름이 일치하지 않습니다.")

	expectedConfigValue := "localhost"
	actualConfigValue := viper.GetString("database.host")
	assert.Equal(t, expectedConfigValue, actualConfigValue, "설정 값이 일치하지 않습니다.")
}

func TestInitDBWithTestContainers(t *testing.T) {
	path := "../../postgres-petstore/docker-compose.yml"
	helper.StartComposeServices(t, path)
	InitConfig()
	InitDB()
	require.NotNil(t, DB, "DB를 초기화해야 합니다")
	sqlDB, err := DB.DB()
	require.NoError(t, err, "DB 연결을 가져오지 못했습니다.")
	require.NoError(t, sqlDB.Ping(), "DB는 통신 할 수 있어야 합니다.")
}
