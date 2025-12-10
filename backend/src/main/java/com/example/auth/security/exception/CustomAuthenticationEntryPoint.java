package com.example.auth.security.exception;

import com.example.auth.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출됩니다.
 * 401 Unauthorized 상태 코드와 함께 공통 ErrorResponse 포맷을 반환합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.error("Unauthorized error: {}", authException.getMessage());

        sendErrorResponse(response, authException);
    }

    private void sendErrorResponse(HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // ErrorResponse 생성 (기존에 정의한 DTO 활용)
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED,
                "인증이 필요합니다: " + authException.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
