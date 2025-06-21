# Board Frontend

Vue.js 3 기반의 게시판 프론트엔드 애플리케이션입니다.

## 기술 스택

- Vue 3 (Composition API)
- TypeScript
- Vue Router
- Pinia
- Axios

## 주요 기능

- 사용자 인증 (로그인, 회원가입)
- 게시글 CRUD (목록, 상세, 작성, 수정, 삭제)
- 댓글 CRUD (작성, 수정, 삭제)
- 라우팅 가드를 통한 접근 제어
- Form 유효성 검사

## 실행 방법

### 1. 의존성 설치

```bash
npm install
```

### 2. 개발 서버 실행

```bash
npm run dev
```

애플리케이션은 `http://localhost:5173` 에서 실행됩니다.

## 프로젝트 구조

- `src/api` - 백엔드 API 통신 관련 로직
- `src/components` - 재사용 가능한 컴포넌트
- `src/router` - Vue Router 설정
- `src/stores` - Pinia 상태 관리
- `src/types` - TypeScript 타입 정의
- `src/views` - 페이지 레벨 컴포넌트

## 연동 백엔드

- Spring Boot 기반의 [Board Backend](https://github.com/your-repo/board-backend) (별도 실행 필요)

## 주요 페이지

- `/` - 홈
- `/login` - 로그인
- `/signup` - 회원가입
- `/posts` - 게시글 목록
- `/posts/create` - 새 글 작성 (로그인 필요)
- `/posts/:id` - 게시글 상세
- `/posts/:id/edit` - 게시글 수정 (작성자만)
