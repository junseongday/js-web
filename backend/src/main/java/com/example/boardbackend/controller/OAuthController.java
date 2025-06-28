package com.example.boardbackend.controller;

import com.example.boardbackend.entity.AuthProvider;
import com.example.boardbackend.entity.User;
import com.example.boardbackend.repository.UserRepository;
import com.example.boardbackend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/oauth")
@CrossOrigin(origins = "http://localhost:5173")
public class OAuthController {

    @Autowired
    private OAuth2AuthorizedClientService clientService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    
    @Value("${oauth2.enabled}")
    private boolean oauth2Enabled;

    @GetMapping("/urls")
    public ResponseEntity<Map<String, String>> getOAuthUrls() {
        Map<String, String> urls = new HashMap<>();
        String baseUrl = "http://localhost:8080";
        
        // Spring Security OAuth2 표준 엔드포인트 사용
        urls.put("google", baseUrl + "/oauth2/authorization/google");
        urls.put("kakao", baseUrl + "/oauth2/authorization/kakao");
        urls.put("naver", baseUrl + "/oauth2/authorization/naver");
        
        return ResponseEntity.ok(urls);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getOAuthStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", oauth2Enabled);
        status.put("providers", new String[]{"google", "kakao", "naver"});
        return ResponseEntity.ok(status);
    }

    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> getOAuthDebugInfo() {
        Map<String, Object> debugInfo = new HashMap<>();
        
        debugInfo.put("googleClientId", googleClientId != null ? googleClientId.substring(0, 20) + "..." : "null");
        debugInfo.put("googleClientSecret", googleClientSecret != null ? "***" + googleClientSecret.substring(Math.max(0, googleClientSecret.length() - 4)) : "null");
        debugInfo.put("redirectUri", "http://localhost:8080/login/oauth2/code/google");
        debugInfo.put("authorizationUrl", "https://accounts.google.com/o/oauth2/v2/auth");
        debugInfo.put("tokenUrl", "https://oauth2.googleapis.com/token");
        debugInfo.put("userInfoUrl", "https://www.googleapis.com/oauth2/v2/userinfo");
        
        return ResponseEntity.ok(debugInfo);
    }

    // Google OAuth2 콘솔 설정 확인을 위한 디버깅 엔드포인트
    @GetMapping("/test-google-config")
    public ResponseEntity<Map<String, Object>> testGoogleConfig() {
        Map<String, Object> result = new HashMap<>();
        
        // Google OAuth2 콘솔에서 확인해야 할 설정들
        result.put("requiredRedirectUri", "http://localhost:8080/login/oauth2/code/google");
        result.put("requiredJavaScriptOrigin", "http://localhost:8080");
        result.put("requiredScopes", Arrays.asList("openid", "profile", "email"));
        result.put("currentClientId", googleClientId != null ? googleClientId.substring(0, 20) + "..." : "null");
        
        return ResponseEntity.ok(result);
    }
} 