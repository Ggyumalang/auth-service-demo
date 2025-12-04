package com.example.auth.security.oauth2;

import com.example.auth.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OAuth2AuthenticationSuccessHandler successHandler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(successHandler, "redirectUri", "http://localhost:3000/oauth2/redirect");
    }

    @Test
    void onAuthenticationSuccess_RedirectsWithToken() throws Exception {
        // Arrange
        String token = "test-jwt-token";
        when(tokenProvider.createToken(authentication)).thenReturn(token);

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).sendRedirect(urlCaptor.capture());

        String redirectUrl = urlCaptor.getValue();
        assertTrue(redirectUrl.startsWith("http://localhost:3000/oauth2/redirect"));
        assertTrue(redirectUrl.contains("token=" + token));
    }
}
