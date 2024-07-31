#!/bin/bash

# 스크립트 실행 디렉토리 설정
SCRIPT_DIR=$(cd "$(dirname "$0")"; pwd)

# OpenAPI 스펙 파일 경로
OPENAPI_FILE="${SCRIPT_DIR}/petstore.yaml"

# 설정 파일 경로
TYPES_CONFIG="${SCRIPT_DIR}/api/types.cfg.yaml"
SERVER_CONFIG="${SCRIPT_DIR}/api/server.cfg.yaml"

# 데이터 모델 코드 생성
echo "Generating Go types from OpenAPI spec..."
oapi-codegen --config=${TYPES_CONFIG} ${OPENAPI_FILE}
if [ $? -ne 0 ]; then
    echo "Code generation for types failed."
    exit 1
fi

# Gin 서버 코드 생성
echo "Generating Go server code from OpenAPI spec..."
oapi-codegen --config=${SERVER_CONFIG} ${OPENAPI_FILE}
if [ $? -ne 0 ]; then
    echo "Code generation for server failed."
    exit 1
fi

echo "Code generation completed successfully."