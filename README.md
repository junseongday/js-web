# Full-Stack Board Application (Spring Boot + Vue.js)

Spring Boot와 Vue.js를 사용한 풀스택 게시판 애플리케이션입니다.

## 프로젝트 소개

이 프로젝트는 백엔드(Spring Boot)와 프론트엔드(Vue.js)가 분리된 모노레포 구조로 구성되어 있습니다. 사용자 인증, 게시글 및 댓글 CRUD 기능을 제공합니다.

## 주요 기능

- **사용자 인증**: JWT 기반의 회원가입, 로그인/로그아웃 기능
- **게시판**: 페이징을 포함한 게시글 목록, 상세 조회, 작성, 수정, 삭제
- **댓글**: 게시글에 대한 댓글 작성, 수정, 삭제
- **권한 관리**: 로그인한 사용자만 글/댓글 작성 가능하며, 본인만 수정/삭제 가능
- **API 문서**: Swagger UI를 통한 API 문서 자동화

## 기술 스택

### Backend (Spring Boot)
- Java 17
- Spring Boot 3.2
- Spring Security & JWT
- Spring Data JPA (Hibernate)
- MySQL
- Gradle

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
MySQL에서 `board_db` 라는 이름의 데이터베이스를 생성하세요.
```sql
CREATE DATABASE board_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
*애플리케이션 실행 시 테이블은 Hibernate에 의해 자동으로 생성됩니다.*


### 2. 백엔드 실행
`backend` 디렉터리로 이동하여 아래 명령어를 실행하세요.
```bash
# 포트: 8080
./gradlew bootRun
```
API 문서는 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) 에서 확인할 수 있습니다.

### 3. 프론트엔드 실행
`frontend` 디렉터리로 이동하여 아래 명령어를 실행하세요.
```bash
# 의존성 설치 (최초 1회)
npm install

# 개발 서버 실행 (포트: 5173)
npm run dev
```
애플리케이션은 [http://localhost:5173](http://localhost:5173) 에서 접속할 수 있습니다.

## API 엔드포인트

자세한 API 명세는 백엔드 실행 후 Swagger UI를 참고하세요.

- **인증 API**: `POST /api/auth/signup`, `POST /api/auth/login`, `GET /api/auth/me`
- **게시글 API**: `GET /api/posts`, `GET /api/posts/{id}`, `POST /api/posts`, `PUT /api/posts/{id}`, `DELETE /api/posts/{id}`
- **댓글 API**: `GET /api/comments/post/{postId}`, `POST /api/comments/post/{postId}`, `PUT /api/comments/{id}`, `DELETE /api/comments/{id}`

## 프로젝트 구조
```
js-web/
├── backend/      # Spring Boot 백엔드
└── frontend/     # Vue.js 프론트엔드
``` 