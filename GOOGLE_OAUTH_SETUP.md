# Google OAuth2 설정 가이드

## 1. Google Cloud Console 설정

### 1.1 프로젝트 생성/선택
1. [Google Cloud Console](https://console.cloud.google.com/)에 접속
2. 새 프로젝트 생성 또는 기존 프로젝트 선택

### 1.2 OAuth 동의 화면 설정
1. "API 및 서비스" > "OAuth 동의 화면" 메뉴로 이동
2. 사용자 유형 선택 (외부 또는 내부)
3. 앱 정보 입력:
   - 앱 이름: "Board App" (또는 원하는 이름)
   - 사용자 지원 이메일: 본인 이메일
   - 개발자 연락처 정보: 본인 이메일

### 1.3 사용자 인증 정보 생성
1. "API 및 서비스" > "사용자 인증 정보" 메뉴로 이동
2. "사용자 인증 정보 만들기" > "OAuth 2.0 클라이언트 ID" 클릭
3. 애플리케이션 유형: "웹 애플리케이션" 선택
4. 이름: "Board App Web Client" (또는 원하는 이름)

### 1.4 승인된 리다이렉트 URI 설정 (중요!)
다음 URI를 **정확히** 추가해야 합니다:

```
http://localhost:8080/login/oauth2/code/google
```

**주의사항:**
- URI 끝에 슬래시(/)가 없어야 함
- 포트 번호(8080)가 정확해야 함
- 프로토콜(http)이 정확해야 함

### 1.5 클라이언트 ID와 시크릿 복사
생성된 클라이언트 ID와 클라이언트 시크릿을 복사하여 `application.yml`에 설정

## 2. application.yml 설정

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: [Google Cloud Console에서 복사한 클라이언트 ID]
            client-secret: [Google Cloud Console에서 복사한 클라이언트 시크릿]
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

## 3. 문제 해결

### 3.1 "invalid_request" 오류
- Google Cloud Console의 리다이렉트 URI가 정확한지 확인
- 클라이언트 ID와 시크릿이 올바른지 확인
- 애플리케이션을 재시작

### 3.2 "redirect_uri_mismatch" 오류
- Google Cloud Console의 리다이렉트 URI와 실제 요청 URI가 정확히 일치하는지 확인
- URI 끝의 슬래시, 포트 번호, 프로토콜 확인

### 3.3 "unauthorized_client" 오류
- 클라이언트 ID가 올바른지 확인
- OAuth 동의 화면이 올바르게 설정되었는지 확인

## 4. 테스트

1. 애플리케이션 재시작
2. 브라우저에서 `http://localhost:5173` 접속
3. Google 로그인 버튼 클릭
4. Google 계정으로 로그인 시도

## 5. 디버깅

애플리케이션 로그에서 다음 정보 확인:
- OAuth2 설정 로드 여부
- 리다이렉트 URI 설정
- 클라이언트 ID/시크릿 설정 여부 