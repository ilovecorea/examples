#!/bin/bash

# Redocly CLI 설치 확인
if ! command -v redocly &> /dev/null
then
    echo "Redocly CLI가 설치되어 있지 않습니다. 설치 후 다시 시도하십시오."
    exit 1
fi

# 번들링할 파일 경로 설정
INPUT_FILE="./openapi/openapi.yml"
OUTPUT_FILE="./board.yml"

# OpenAPI 파일 유효성 검사
echo "OpenAPI 파일 유효성 검사 중..."
redocly lint ${INPUT_FILE}

if [ $? -ne 0 ]; then
  echo "유효성 검사 과정에서 오류가 발생했습니다."
  exit 1
fi

echo "유효성 검사 완료."

# 번들링 수행
echo "OpenAPI 파일들을 번들링 중..."
redocly bundle -o ${OUTPUT_FILE} ${INPUT_FILE}

if [ $? -ne 0 ]; then
  echo "번들링 과정에서 오류가 발생했습니다."
  exit 1
fi

echo "번들링 완료: ${OUTPUT_FILE}"

# 문서 생성할 출력 파일 설정
DOCS_OUTPUT="index.html"  # 파일명으로 지정
DOCS_DIR="./docs"

# DOCS_DIR 디렉토리 존재 여부 확인 및 생성
if [ ! -d "${DOCS_DIR}" ]; then
  mkdir -p ${DOCS_DIR}
fi

# 문서 생성 수행
echo "문서 생성 중..."
redocly build-docs ${OUTPUT_FILE} -o ${DOCS_OUTPUT}

if [ $? -ne 0 ]; then
  echo "문서 생성 과정에서 오류가 발생했습니다."
  exit 1
fi

echo "문서 생성 완료: ${DOCS_OUTPUT}"

# HTML 파일을 docs 디렉토리로 이동
mv ${DOCS_OUTPUT} ${DOCS_DIR}

if [ $? -ne 0 ]; then
  echo "파일 이동 중 오류가 발생했습니다."
  exit 1
fi

echo "HTML 파일이 ${DOCS_DIR}/index.html로 이동되었습니다."