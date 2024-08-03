# Demo

1. Docker 컨테이너 구동
```shell
docker-compose up --build
```

2. OAuth2 클라이언트를 생성
```shell
curl --request POST \
  --url http://127.0.0.1:4445/admin/clients \
  --header 'Content-Type: application/json' \
  --data '{
  "grant_types": [
    "authorization_code",
    "refresh_token"
  ],
  "redirect_uris": [
    "http://127.0.0.1:5555/callback"
  ],
  "response_types": [
    "code",
    "id_token"
  ],
  "scope": "openid offline",
  "token_endpoint_auth_method": "none"
}'
```

3. 브라우저에서 인증 코드 요청
```shell
http://127.0.0.1:4444/oauth2/auth?client_id=$CLIENT_ID&redirect_uri=http%3A%2F%2F127.0.0.1%3A5555%2Fcallback&response_type=code&state=1102398157&scope=offline%20openid
```

4. 사용자를 등록하고 code과 client_id 를 복사
```shell
curl --request POST \
  --url http://127.0.0.1:4444/oauth2/token \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data client_id=$CLIENT_ID \
  --data code=$AUTH_CODE \
  --data grant_type=authorization_code \
  --data redirect_uri=http://127.0.0.1:5555/callback
```