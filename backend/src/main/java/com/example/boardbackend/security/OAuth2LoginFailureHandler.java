package com.example.boardbackend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public OAuth2LoginFailureHandler() {
        super("http://localhost:5173/login?error=oauth_failed");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                      AuthenticationException exception) throws IOException, ServletException {
        
        System.out.println("=== OAuth2 Login Failure ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Exception: " + exception.getMessage());
        System.out.println("Exception Type: " + exception.getClass().getSimpleName());
        
        // 프론트엔드로 리다이렉트 (오류 정보 포함)
        String redirectUrl = UriComponentsBuilder
            .fromUriString("http://localhost:5173/login")
            .queryParam("error", "oauth_failed")
            .queryParam("message", exception.getMessage())
            .build()
            .toUriString();
        
        System.out.println("Redirect to: " + redirectUrl);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
} 