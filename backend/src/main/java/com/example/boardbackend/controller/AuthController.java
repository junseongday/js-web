package com.example.boardbackend.controller;

import com.example.boardbackend.dto.UserDto;
import com.example.boardbackend.security.JwtTokenProvider;
import com.example.boardbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "사용자 인증 API")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<UserDto.UserInfo> signup(@Valid @RequestBody UserDto.SignupRequest signupRequest) {
        UserDto.UserInfo userInfo = userService.signup(signupRequest);
        return ResponseEntity.ok(userInfo);
    }
    
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 로그인을 처리하고 JWT 토큰을 반환합니다.")
    public ResponseEntity<UserDto.LoginResponse> login(@Valid @RequestBody UserDto.LoginRequest loginRequest) {
        // 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // JWT 토큰 생성
        String jwt = tokenProvider.generateToken(authentication);
        
        // 로그인 응답 생성
        UserDto.LoginResponse loginResponse = userService.login(loginRequest);
        loginResponse.setToken(jwt);
        
        return ResponseEntity.ok(loginResponse);
    }
    
    @GetMapping("/me")
    @Operation(summary = "현재 사용자 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다.")
    public ResponseEntity<UserDto.UserInfo> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenProvider.getEmailFromJWT(token);
        String provider = tokenProvider.getProviderFromJWT(token);
        UserDto.UserInfo userInfo = userService.getCurrentUser(email, provider);
        return ResponseEntity.ok(userInfo);
    }
} 