# Full-Stack Board Application (Spring Boot + Vue.js)

Spring Boot와 Vue.js를 사용한 풀스택 게시판 애플리케이션입니다.

## 프로젝트 소개

이 프로젝트는 백엔드(Spring Boot)와 프론트엔드(Vue.js)가 분리된 모노레포 구조로 구성되어 있습니다. **JWT 인증**과 **Google, Kakao, Naver 소셜 로그인(OAuth2)**, 게시글/댓글 CRUD 기능을 제공합니다.

## 주요 기능

- **사용자 인증**: JWT 기반 회원가입, 로그인/로그아웃, 소셜 로그인(Google, Kakao, Naver)
- **게시판**: 페이징 포함 게시글 목록, 상세, 작성, 수정, 삭제
- **댓글**: 게시글별 댓글 작성, 수정, 삭제
- **권한 관리**: 로그인한 사용자만 글/댓글 작성, 본인만 수정/삭제 가능
- **API 문서**: Swagger UI 자동화

## 기술 스택

### Backend (Spring Boot)
- Java 17
- Spring Boot 3.2
- Spring Security & JWT
- Spring Data JPA (Hibernate)
- MySQL
- Gradle
- OAuth2 (Google, Kakao, Naver)

### Frontend (Vue.js)
- Vue 3 (Composition API)
- TypeScript
- Vite
- Vue Router
- Pinia (상태 관리)
- Axios (HTTP 통신)

## 실행 방법

이 프로젝트는 백엔드와 프론트엔드를 각각 실행해야 합니다.

### 1. 데이터베이스 설정
```bash
docker run -d \
  --name mysql-board \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=board_db \
  -p 3306:3306 \
  mysql:8.0 \
  --character-set-server=utf8mb4 \
  --collation-server=utf8mb4_unicode_ci \
  --default-time-zone='+00:00'
```
MySQL에서 `board_db` 데이터베이스를 생성하세요.
```sql
CREATE DATABASE board_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
*애플리케이션 실행 시 테이블은 Hibernate에 의해 자동 생성됩니다.*

### 2. OAuth2 소셜 로그인 환경설정

- Google, Kakao, Naver 개발자 콘솔에서 각각 Client ID/Secret을 발급받아야 합니다.
- **Redirect URI는 반드시 아래와 같이 백엔드(8080)로 설정해야 합니다:**
  - `http://localhost:8080/login/oauth2/code/google`
  - `http://localhost:8080/login/oauth2/code/kakao`
  - `http://localhost:8080/login/oauth2/code/naver`
- 아래 예시처럼 `backend/src/main/resources/application.yml`에 실제 값을 입력하세요:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: [발급받은 Google Client ID]
            client-secret: [발급받은 Google Client Secret]
            scope: openid, profile, email
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
          kakao:
            client-id: [발급받은 Kakao Client ID]
            client-secret: [발급받은 Kakao Client Secret]
            client-authentication-method: client_secret_post
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: profile, account_email
            provider: kakao
          naver:
            client-id: [발급받은 Naver Client ID]
            client-secret: [발급받은 Naver Client Secret]
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
```

- **자세한 설정법/문제해결은 아래 문서를 참고하세요:**
  - [OAUTH_SETUP.md](./OAUTH_SETUP.md) (상세 가이드)
  - [OAUTH_QUICK_SETUP.md](./OAUTH_QUICK_SETUP.md) (빠른 시작)
  - [GOOGLE_OAUTH_SETUP.md](./GOOGLE_OAUTH_SETUP.md) (Google 전용)

### 3. 백엔드 실행
`backend` 디렉터리에서:
```bash
# 포트: 8080
./gradlew bootRun
```
API 문서는 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) 에서 확인할 수 있습니다.

### 4. 프론트엔드 실행
`frontend` 디렉터리에서:
```bash
# 의존성 설치 (최초 1회)
npm install
# 개발 서버 실행 (포트: 5173)
npm run dev
```
애플리케이션은 [http://localhost:5173](http://localhost:5173) 에서 접속할 수 있습니다.

## 주요 API 엔드포인트

- **인증 API**: `POST /api/auth/signup`, `POST /api/auth/login`, `GET /api/auth/me`
- **게시글 API**: `GET /api/posts`, `GET /api/posts/{id}`, `POST /api/posts`, `PUT /api/posts/{id}`, `DELETE /api/posts/{id}`
- **댓글 API**: `GET /api/comments/post/{postId}`, `POST /api/comments/post/{postId}`, `PUT /api/comments/{id}`, `DELETE /api/comments/{id}`
- **소셜 로그인 관련**:
  - `GET /api/oauth/urls` : 사용 가능한 소셜 로그인 URL 목록
  - `GET /api/oauth/status` : 소셜 로그인 활성화 상태

자세한 API 명세는 Swagger UI 참고.

## 문제해결 & 참고
- 소셜 로그인 버튼이 비활성화되거나, [invalid_request], [redirect_uri_mismatch] 등 오류 발생 시:
  - [OAUTH_SETUP.md](./OAUTH_SETUP.md) 및 [GOOGLE_OAUTH_SETUP.md](./GOOGLE_OAUTH_SETUP.md) 참고
  - Redirect URI, Client ID/Secret, application.yml 설정, 서버 재시작 여부 확인
- 기타 문의: 이슈 등록 또는 README 내 문서 참고

## 프로젝트 구조
```
js-web/
├── backend/      # Spring Boot 백엔드
└── frontend/     # Vue.js 프론트엔드
``` 
