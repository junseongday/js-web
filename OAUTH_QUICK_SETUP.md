# OAuth 설정 빠른 가이드

## 🚨 중요: 리다이렉트 URI 설정

OAuth 제공자에서 **반드시 백엔드 URL로 리다이렉트 URI를 설정**해야 합니다.

### ❌ 잘못된 설정
- `http://localhost:5173/oauth/redirect` (프론트엔드)
- `http://localhost:5173/login/oauth2/code/google`

### ✅ 올바른 설정
- `http://localhost:8080/login/oauth2/code/google`
- `http://localhost:8080/login/oauth2/code/kakao`
- `http://localhost:8080/login/oauth2/code/naver`

## OAuth 흐름

```
프론트엔드(5173) → OAuth 제공자 → 백엔드(8080) → 프론트엔드(5173)
```

1. 사용자가 프론트엔드에서 OAuth 버튼 클릭
2. OAuth 제공자로 리다이렉트
3. 사용자가 OAuth 제공자에서 로그인
4. **OAuth 제공자가 백엔드(8080)로 리다이렉트** ⭐
5. 백엔드에서 JWT 생성 후 프론트엔드(5173)로 리다이렉트
6. 프론트엔드에서 JWT 받아 로그인 완료

## 각 OAuth 제공자 설정

### Google
- **승인된 리디렉션 URI**: `http://localhost:8080/login/oauth2/code/google`

### Kakao
- **Redirect URI**: `http://localhost:8080/login/oauth2/code/kakao`

### Naver
- **Callback URL**: `http://localhost:8080/login/oauth2/code/naver`

## application.yml 설정

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: your-actual-google-client-id.apps.googleusercontent.com
            client-secret: your-actual-google-client-secret
            scope: openid, profile, email
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
          kakao:
            client-id: your-actual-kakao-client-id
            client-secret: your-actual-kakao-client-secret
            client-authentication-method: client_secret_post
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: profile, account_email
            provider: kakao
          naver:
            client-id: your-actual-naver-client-id
            client-secret: your-actual-naver-client-secret
            client-authentication-method: client_secret_post
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: name, email
            provider: naver
        provider:
          google:
            authorization-uri: https://accounts.google.com/o/oauth2/v2/auth
            token-uri: https://oauth2.googleapis.com/token
            user-info-uri: https://www.googleapis.com/oauth2/v2/userinfo
            user-name-attribute: sub
          kakao:
            authorization-uri: https://kauth.kakao.com/oauth/authorize
            token-uri: https://kauth.kakao.com/oauth/token
            user-info-uri: https://kapi.kakao.com/v2/user/me
            user-name-attribute: id
          naver:
            authorization-uri: https://nid.naver.com/oauth2.0/authorize
            token-uri: https://nid.naver.com/oauth2.0/token
            user-info-uri: https://openapi.naver.com/v1/nid/me
            user-name-attribute: response

# OAuth2 활성화 플래그
oauth2:
  enabled: true
```

## 문제 해결

### OAuth 버튼이 비활성화된 경우
1. `oauth2.enabled: true` 설정 확인
2. 실제 OAuth 클라이언트 ID/시크릿 입력 (test- 또는 your-로 시작하지 않는 값)
3. 서버 재시작

### 리다이렉트 URI 오류
1. OAuth 제공자 설정에서 백엔드 URL(8080)로 설정했는지 확인
2. URI 경로가 정확한지 확인: `/login/oauth2/code/{provider}` 