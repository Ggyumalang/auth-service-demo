package com.example.auth.config;

/**
 * Permit ALL URL 설정
 */
public class AuthConfig {
    public static final String[] PERMIT_ALL = {
            "/api/auth/**",
            "/api/crypto/**", // 암호화 공개키 엔드포인트
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
}
