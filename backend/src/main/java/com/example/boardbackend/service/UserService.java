package com.example.boardbackend.service;

import com.example.boardbackend.dto.UserDto;
import com.example.boardbackend.entity.AuthProvider;
import com.example.boardbackend.entity.User;
import com.example.boardbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // JwtAuthenticationFilter에서 provider 정보도 JWT에 넣고, UserService에서 email+provider로 조회
    @Override
    public UserDetails loadUserByUsername(String emailAndProvider) throws UsernameNotFoundException {
        // emailAndProvider: "email|provider" 형태로 전달
        String[] parts = emailAndProvider.split("\\|");
        String email = parts[0];
        String provider = parts.length > 1 ? parts[1] : "LOCAL";

        User user = userRepository.findByEmailAndAuthProvider(email, AuthProvider.valueOf(provider))
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email + " and provider: " + provider));
        return user;
    }
    
    public UserDto.UserInfo signup(UserDto.SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        
        if (userRepository.existsByNickname(signupRequest.getNickname())) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }
        
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setNickname(signupRequest.getNickname());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        
        User savedUser = userRepository.save(user);
        
        return new UserDto.UserInfo(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname());
    }
    
    public UserDto.LoginResponse login(UserDto.LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 잘못되었습니다."));
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 잘못되었습니다.");
        }
        
        // JWT 토큰은 AuthController에서 생성
        return new UserDto.LoginResponse(null, user.getEmail(), user.getNickname(), user.getId());
    }
    
    public UserDto.UserInfo getCurrentUser(String email, String provider) {
        User user = userRepository.findByEmailAndAuthProvider(email, AuthProvider.valueOf(provider))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return new UserDto.UserInfo(user.getId(), user.getEmail(), user.getNickname());
    }
} 