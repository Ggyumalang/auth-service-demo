package com.example.auth.security.oauth2;

import com.example.auth.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
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
        successHandler = new OAuth2AuthenticationSuccessHandler(tokenProvider);
        ReflectionTestUtils.setField(successHandler, "redirectUri", "http://localhost:3000/oauth2/redirect");
    }

    @Test
    void onAuthenticationSuccess_RedirectsWithToken() throws Exception {
        // Arrange
        String token = "test-jwt-token";
        when(tokenProvider.createToken(authentication)).thenReturn(token);
        when(response.encodeRedirectURL(anyString())).thenAnswer(i -> i.getArgument(0));

        final String[] capturedUrl = new String[1];
        doAnswer(invocation -> {
            capturedUrl[0] = invocation.getArgument(0);
            System.out.println("DEBUG: response.sendRedirect called with: " + capturedUrl[0]);
            return null;
        }).when(response).sendRedirect(anyString());

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(response).sendRedirect(anyString());

        String redirectUrl = capturedUrl[0];
        System.out.println("DEBUG: Captured redirectUrl: " + redirectUrl);

        if (redirectUrl == null) {
            // 만약 null이라면 ReflectionTestUtils로 설정한 값이 제대로 들어갔는지 확인
            Object uriField = ReflectionTestUtils.getField(successHandler, "redirectUri");
            System.out.println("DEBUG: successHandler.redirectUri field value: " + uriField);
        }

        assertNotNull(redirectUrl, "Redirect URL should not be null");
        assertTrue(redirectUrl.startsWith("http://localhost:3000/oauth2/redirect"));
        assertTrue(redirectUrl.contains("token=" + token));
    }
}
