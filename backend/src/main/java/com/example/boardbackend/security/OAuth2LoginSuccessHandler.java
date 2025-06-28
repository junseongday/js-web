package com.example.boardbackend.security;

import com.example.boardbackend.entity.AuthProvider;
import com.example.boardbackend.entity.User;
import com.example.boardbackend.repository.UserRepository;
import com.example.boardbackend.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;
    private ObjectMapper objectMapper;

    public void setJwtTokenProvider(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        System.out.println("=== OAuth2 Login Success ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Authentication: " + authentication);
        
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId();
        
        System.out.println("Provider: " + provider);
        System.out.println("OAuth2User: " + oauth2User);
        System.out.println("OAuth2User Attributes: " + oauth2User.getAttributes());
        
        // OAuth2 사용자 정보 추출
        OAuth2UserInfo userInfo = extractUserInfo(oauth2User, provider);
        System.out.println("Extracted UserInfo: " + userInfo);
        
        // 기존 사용자 확인 또는 새 사용자 생성
        User user = findOrCreateUser(userInfo, provider);
        System.out.println("User: " + user);
        
        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(user);
        System.out.println("Generated JWT Token: " + token.substring(0, 20) + "...");
        
        // 프론트엔드로 리다이렉트 (JWT 포함)
        String redirectUrl = UriComponentsBuilder
            .fromUriString("http://localhost:5173/oauth/redirect")
            .queryParam("token", token)
            .queryParam("provider", provider)
            .build()
            .toUriString();
        
        System.out.println("Redirect URL: " + redirectUrl);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private OAuth2UserInfo extractUserInfo(OAuth2User oauth2User, String provider) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        switch (provider.toLowerCase()) {
            case "google":
                return OAuth2UserInfo.builder()
                    .providerId(attributes.get("sub").toString())
                    .email((String) attributes.get("email"))
                    .nickname((String) attributes.get("name"))
                    .profileImage((String) attributes.get("picture"))
                    .build();
                    
            case "kakao":
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                return OAuth2UserInfo.builder()
                    .providerId(attributes.get("id").toString())
                    .email((String) kakaoAccount.get("email"))
                    .nickname((String) profile.get("nickname"))
                    .profileImage((String) profile.get("profile_image_url"))
                    .build();
                    
            case "naver":
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                return OAuth2UserInfo.builder()
                    .providerId(response.get("id").toString())
                    .email((String) response.get("email"))
                    .nickname((String) response.get("nickname"))
                    .profileImage((String) response.get("profile_image"))
                    .build();
                    
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }

    private User findOrCreateUser(OAuth2UserInfo userInfo, String provider) {
        // 1. providerId로 기존 사용자 찾기
        Optional<User> existingUser = userRepository.findByProviderId(userInfo.getProviderId());
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        // 2. provider + email로 기존 사용자 찾기 (동일 provider에서만)
        if (userInfo.getEmail() != null) {
            Optional<User> userByEmailAndProvider = userRepository.findByEmailAndAuthProvider(userInfo.getEmail(),
                AuthProvider.valueOf(provider.toUpperCase()));
            if (userByEmailAndProvider.isPresent()) {
                return userByEmailAndProvider.get();
            }
        }

        // 3. 새 사용자 생성 (동일 이메일이어도 provider가 다르면 새로 생성)
        User newUser = new User();
        newUser.setEmail(userInfo.getEmail());
        newUser.setNickname(userInfo.getNickname() != null ? userInfo.getNickname() : generateNickname(userInfo.getProviderId(), provider));
        newUser.setAuthProvider(AuthProvider.valueOf(provider.toUpperCase()));
        newUser.setProviderId(userInfo.getProviderId());
        newUser.setProfileImage(userInfo.getProfileImage());
        newUser.setPassword(""); // OAuth 사용자는 비밀번호 없음

        return userRepository.save(newUser);
    }

    private String generateNickname(String providerId, String provider) {
        return provider.substring(0, 1).toUpperCase() + provider.substring(1) + "_" + providerId.substring(0, 8);
    }

    // OAuth2 사용자 정보를 담는 내부 클래스
    public static class OAuth2UserInfo {
        private String providerId;
        private String email;
        private String nickname;
        private String profileImage;

        public OAuth2UserInfo() {}

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private OAuth2UserInfo userInfo = new OAuth2UserInfo();

            public Builder providerId(String providerId) {
                userInfo.providerId = providerId;
                return this;
            }

            public Builder email(String email) {
                userInfo.email = email;
                return this;
            }

            public Builder nickname(String nickname) {
                userInfo.nickname = nickname;
                return this;
            }

            public Builder profileImage(String profileImage) {
                userInfo.profileImage = profileImage;
                return this;
            }

            public OAuth2UserInfo build() {
                return userInfo;
            }
        }

        // Getters
        public String getProviderId() { return providerId; }
        public String getEmail() { return email; }
        public String getNickname() { return nickname; }
        public String getProfileImage() { return profileImage; }
    }
} 