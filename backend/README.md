# Board Backend

Spring Boot 기반의 게시판 백엔드 애플리케이션입니다.

## 기술 스택

- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA (Hibernate)
- MySQL
- JWT (JSON Web Token)
- Swagger/OpenAPI 3
- Gradle

## 주요 기능

- 사용자 인증 (회원가입, 로그인, JWT 기반 인증)
- 게시글 CRUD (작성, 조회, 수정, 삭제)
- 댓글 CRUD (작성, 조회, 수정, 삭제)
- 페이징 처리
- 권한 기반 접근 제어

## 실행 방법

### 1. MySQL 데이터베이스 설정

MySQL에서 `board_db` 데이터베이스를 생성하고, `application.yml`에서 데이터베이스 연결 정보를 설정하세요.

```sql
CREATE DATABASE board_db;
```

### 2. 애플리케이션 실행

```bash
# Gradle을 사용하여 실행
./gradlew bootRun

# 또는 JAR 파일로 빌드 후 실행
./gradlew clean build
java -jar build/libs/board-backend-0.0.1-SNAPSHOT.jar
```

### 3. API 문서 확인

애플리케이션 실행 후 다음 URL에서 Swagger UI를 확인할 수 있습니다:
- http://localhost:8080/swagger-ui/index.html

## API 엔드포인트

### 인증 API
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/login` - 로그인
- `GET /api/auth/me` - 현재 사용자 정보 조회

### 게시글 API
- `GET /api/posts` - 게시글 목록 조회 (페이징)
- `GET /api/posts/{postId}` - 게시글 상세 조회
- `POST /api/posts` - 게시글 작성 (인증 필요)
- `PUT /api/posts/{postId}` - 게시글 수정 (작성자만)
- `DELETE /api/posts/{postId}` - 게시글 삭제 (작성자만)

### 댓글 API
- `GET /api/comments/post/{postId}` - 댓글 목록 조회
- `POST /api/comments/post/{postId}` - 댓글 작성 (인증 필요)
- `PUT /api/comments/{commentId}` - 댓글 수정 (작성자만)
- `DELETE /api/comments/{commentId}` - 댓글 삭제 (작성자만)

## 보안

- JWT 토큰 기반 인증
- BCrypt를 사용한 비밀번호 해싱
- CORS 설정 (http://localhost:5173 허용)
- 권한 기반 접근 제어

## 데이터베이스 스키마

애플리케이션 실행 시 Hibernate가 자동으로 테이블을 생성합니다:

- `users` - 사용자 정보
- `posts` - 게시글 정보
- `comments` - 댓글 정보 