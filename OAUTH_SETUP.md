# OAuth 2.0 설정 가이드

이 프로젝트는 Google, Kakao, Naver OAuth 2.0 로그인을 지원합니다.

## 🚨 현재 상황

현재 OAuth 설정이 완료되지 않아 소셜 로그인 버튼이 비활성화되어 있습니다. 
아래 가이드를 따라 실제 OAuth 설정을 완료하세요.

## 🔄 OAuth 흐름 설명

```
1. 사용자가 프론트엔드(http://localhost:5173)에서 OAuth 버튼 클릭
2. OAuth 제공자(Google/Kakao/Naver)로 리다이렉트
3. 사용자가 OAuth 제공자에서 로그인
4. OAuth 제공자가 백엔드(http://localhost:8080/login/oauth2/code/{provider})로 리다이렉트
5. 백엔드에서 JWT 토큰 생성 후 프론트엔드(http://localhost:5173/oauth/redirect)로 리다이렉트
6. 프론트엔드에서 JWT 토큰을 받아 로그인 완료
```

## 1. Google OAuth 설정

### 1.1 Google Cloud Console에서 프로젝트 생성
1. [Google Cloud Console](https://console.cloud.google.com/)에 접속
2. 새 프로젝트 생성 또는 기존 프로젝트 선택
3. "API 및 서비스" > "사용자 인증 정보"로 이동

### 1.2 OAuth 2.0 클라이언트 ID 생성
1. "사용자 인증 정보 만들기" > "OAuth 2.0 클라이언트 ID" 선택
2. 애플리케이션 유형: "웹 애플리케이션" 선택
3. **승인된 리디렉션 URI 추가**:
   - `http://localhost:8080/login/oauth2/code/google` ⭐ **중요**
4. 클라이언트 ID와 클라이언트 보안 비밀번호 복사

### 1.3 application.yml 설정
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
        provider:
          google:
            authorization-uri: https://accounts.google.com/o/oauth2/v2/auth
            token-uri: https://oauth2.googleapis.com/token
            user-info-uri: https://www.googleapis.com/oauth2/v2/userinfo
            user-name-attribute: sub

# OAuth2 활성화 플래그
oauth2:
  enabled: true
```

## 2. Kakao OAuth 설정

### 2.1 Kakao Developers에서 애플리케이션 생성
1. [Kakao Developers](https://developers.kakao.com/)에 접속
2. "내 애플리케이션" > "애플리케이션 추가하기"
3. 앱 이름과 회사명 입력

### 2.2 플랫폼 설정
1. "플랫폼" > "Web" 플랫폼 등록
2. 사이트 도메인: `http://localhost:8080`
3. "카카오 로그인" > "활성화 설정" > "활성화"
4. **"카카오 로그인" > "Redirect URI" 설정**:
   - `http://localhost:8080/login/oauth2/code/kakao` ⭐ **중요**

### 2.3 보안 설정
1. "보안" > "Client Secret" 생성
2. "카카오 로그인" > "동의항목" 설정:
   - 닉네임 (profile_nickname)
   - 이메일 (account_email)

### 2.4 application.yml 설정
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: your-actual-kakao-client-id
            client-secret: your-actual-kakao-client-secret
            client-authentication-method: client_secret_post
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: profile, account_email
            provider: kakao
        provider:
          kakao:
            authorization-uri: https://kauth.kakao.com/oauth/authorize
            token-uri: https://kauth.kakao.com/oauth/token
            user-info-uri: https://kapi.kakao.com/v2/user/me
            user-name-attribute: id

# OAuth2 활성화 플래그
oauth2:
  enabled: true
```

## 3. Naver OAuth 설정

### 3.1 Naver Developers에서 애플리케이션 생성
1. [Naver Developers](https://developers.naver.com/)에 접속
2. "Application" > "애플리케이션 등록"
3. 애플리케이션 이름과 사용 API 선택

### 3.2 환경 설정
1. "환경추가" > "Web 서비스 URL" 설정:
   - 서비스 URL: `http://localhost:8080`
   - **Callback URL: `http://localhost:8080/login/oauth2/code/naver`** ⭐ **중요**

### 3.3 application.yml 설정
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          naver:
            client-id: your-actual-naver-client-id
            client-secret: your-actual-naver-client-secret
            client-authentication-method: client_secret_post
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: name, email
            provider: naver
        provider:
          naver:
            authorization-uri: https://nid.naver.com/oauth2.0/authorize
            token-uri: https://nid.naver.com/oauth2.0/token
            user-info-uri: https://openapi.naver.com/v1/nid/me
            user-name-attribute: response

# OAuth2 활성화 플래그
oauth2:
  enabled: true
```

## 4. 설정 적용 방법

### 4.1 application.yml 수정
`backend/src/main/resources/application.yml` 파일에서 OAuth 설정을 실제 값으로 교체:

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

### 4.2 서버 재시작
설정 변경 후 백엔드 서버를 재시작하세요.

## 5. 테스트

### 5.1 백엔드 실행
```bash
cd backend
./gradlew bootRun
```

### 5.2 프론트엔드 실행
```bash
cd frontend
npm run dev
```

### 5.3 로그인 테스트
1. `http://localhost:5173/login` 접속
2. OAuth 버튼이 활성화되었는지 확인
3. 각 OAuth 버튼 클릭하여 테스트

## 6. 문제 해결

### 6.1 일반적인 오류
- **리디렉션 URI 불일치**: OAuth 제공자 설정에서 리디렉션 URI가 정확히 일치하는지 확인
  - Google: `http://localhost:8080/login/oauth2/code/google`
  - Kakao: `http://localhost:8080/login/oauth2/code/kakao`
  - Naver: `http://localhost:8080/login/oauth2/code/naver`
- **CORS 오류**: 백엔드 CORS 설정 확인
- **토큰 파싱 오류**: JWT 토큰 형식 확인

### 6.2 로그 확인
백엔드 로그에서 OAuth 관련 오류 메시지 확인:
```yaml
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.security.oauth2: DEBUG
    com.example.boardbackend: DEBUG
```

### 6.3 OAuth 버튼이 비활성화된 경우
- OAuth 설정이 올바르게 입력되었는지 확인
- `oauth2.enabled: true` 설정 확인
- 서버 재시작 후 다시 확인

### 6.4 리다이렉트 URI 오류
- OAuth 제공자 설정에서 리다이렉트 URI가 `http://localhost:8080/login/oauth2/code/{provider}`로 설정되어 있는지 확인
- 프론트엔드 URL(`http://localhost:5173`)이 아닌 백엔드 URL(`http://localhost:8080`)로 설정해야 함

## 7. 보안 고려사항

1. **클라이언트 시크릿 보호**: 절대 소스 코드에 하드코딩하지 마세요
2. **HTTPS 사용**: 프로덕션 환경에서는 반드시 HTTPS 사용
3. **토큰 만료**: JWT 토큰의 적절한 만료 시간 설정
4. **CSRF 보호**: OAuth 로그인에서도 CSRF 공격 방지 고려

## 8. 환경 변수 사용 (권장)

프로덕션 환경에서는 환경 변수를 사용하여 OAuth 설정을 관리하는 것을 권장합니다:

```bash
export GOOGLE_CLIENT_ID=your-google-client-id
export GOOGLE_CLIENT_SECRET=your-google-client-secret
export KAKAO_CLIENT_ID=your-kakao-client-id
export KAKAO_CLIENT_SECRET=your-kakao-client-secret
export NAVER_CLIENT_ID=your-naver-client-id
export NAVER_CLIENT_SECRET=your-naver-client-secret
```

그리고 application.yml에서:
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: openid, profile, email
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
          kakao:
            client-id: ${KAKAO_CLIENT_ID}
            client-secret: ${KAKAO_CLIENT_SECRET}
            client-authentication-method: client_secret_post
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: profile, account_email
            provider: kakao
          naver:
            client-id: ${NAVER_CLIENT_ID}
            client-secret: ${NAVER_CLIENT_SECRET}
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