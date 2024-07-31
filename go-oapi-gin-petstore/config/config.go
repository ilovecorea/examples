package config

import (
	"fmt"
	"github.com/spf13/viper"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"log"
	"os"
)

// DB 는 전역 DB 변수를 정의하여 다른 패키지에서 데이터베이스 연결을 사용할 수 있게 합니다.
var DB *gorm.DB

// InitConfig 함수는 환경 변수를 통해 설정 파일을 읽어들입니다.
func InitConfig() {
	// 환경 변수 "GO_PROFILE"을 가져옵니다. 기본값은 "local"입니다.
	profile := os.Getenv("GO_PROFILE")
	if profile == "" {
		profile = "local"
	}
	fmt.Println("사용할 설정 프로파일:", profile)

	// 설정 파일의 이름과 형식을 지정합니다.
	viper.SetConfigName(fmt.Sprintf("cfg.%s", profile))
	viper.SetConfigType("yaml")
	// 설정 파일이 위치한 경로를 추가합니다.
	viper.AddConfigPath("../config")
	// 환경 변수를 자동으로 읽어옵니다.
	viper.AutomaticEnv()

	// 설정 파일을 읽어들입니다.
	if err := viper.ReadInConfig(); err != nil {
		log.Fatalf("설정 파일을 읽는 도중 오류 발생: %v", err)
	}
}

// InitDB 함수는 데이터베이스 연결을 초기화합니다.
func InitDB() {
	// 설정 파일에서 데이터베이스 연결 정보를 가져옵니다.
	host := viper.GetString("database.host")
	port := viper.GetString("database.port")
	username := viper.GetString("database.username")
	password := viper.GetString("database.password")
	dbname := viper.GetString("database.name")

	// 필수 필드가 누락된 경우 오류를 발생시킵니다.
	if host == "" || port == "" || username == "" || password == "" || dbname == "" {
		log.Fatalf("데이터베이스 설정이 누락되었습니다. host=%s, port=%s, user=%s, dbname=%s",
			host, port, username, dbname)
	}

	// 데이터베이스 연결 문자열을 생성합니다.
	dsn := fmt.Sprintf("host=%s user=%s password=%s dbname=%s port=%s sslmode=disable",
		host, username, password, dbname, port)

	// 데이터베이스에 연결을 시도합니다.
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		log.Fatalf("데이터베이스에 연결하는 도중 오류 발생: %v", err)
	}

	// 전역 DB 변수에 연결을 저장합니다.
	DB = db
}
