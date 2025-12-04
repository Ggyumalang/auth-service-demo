package com.example.auth.security.oauth2;

import com.example.auth.domain.user.User;
import com.example.auth.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    @Spy
    private CustomOAuth2UserService customOAuth2UserService;

    private OAuth2UserRequest oAuth2UserRequest;
    private OAuth2User oAuth2User;

    @BeforeEach
    void setUp() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("kakao")
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/kakao")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .clientName("Kakao")
                .build();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "test-access-token",
                java.time.Instant.now(),
                java.time.Instant.now().plusSeconds(3600));

        oAuth2UserRequest = new OAuth2UserRequest(clientRegistration, accessToken);

        Map<String, Object> attributes = Map.of(
                "id", 123456789L,
                "kakao_account", Map.of("email", "test@example.com"));
        oAuth2User = new DefaultOAuth2User(Collections.emptySet(), attributes, "id");
    }

    @Test
    void loadUser_Kakao_NewUser_Success() {
        // Arrange
        doReturn(oAuth2User).when(customOAuth2UserService).loadUserDelegate(any(OAuth2UserRequest.class));
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.empty());

        // Act
        OAuth2User result = customOAuth2UserService.loadUser(oAuth2UserRequest);

        // Assert
        assertNotNull(result);
        System.out.println("result.getName = " + result.getName());
        System.out.println("result.getAttributes = " + result.getAttributes());
        System.out.println("result.getAuthorities = " + result.getAuthorities());
        verify(userRepository).save(any(User.class));
        assertThat(result.getName()).isEqualTo("test@example.com");
    }

    @Test
    void loadUser_Naver_ExistingUser_Success() {
        // Arrange
        ClientRegistration naverRegistration = ClientRegistration.withRegistrationId("naver")
                .clientId("naver-client-id")
                .clientSecret("naver-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/naver")
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                .tokenUri("https://nid.naver.com/oauth2.0/token")
                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                .userNameAttributeName("response")
                .clientName("Naver")
                .build();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER,
                "naver-access-token",
                java.time.Instant.now(),
                java.time.Instant.now().plusSeconds(3600));

        OAuth2UserRequest naverRequest = new OAuth2UserRequest(naverRegistration, accessToken);

        Map<String, Object> response = Map.of("email", "naver@example.com");
        Map<String, Object> attributes = Map.of("response", response);
        OAuth2User naverUser = new DefaultOAuth2User(Collections.emptySet(), attributes, "response");

        doReturn(naverUser).when(customOAuth2UserService).loadUserDelegate(any(OAuth2UserRequest.class));

        User existingUser = User.builder()
                .username("naver@example.com")
                .email("naver@example.com")
                .password("password")
                .build();
        when(userRepository.findByUsername("naver@example.com")).thenReturn(Optional.of(existingUser));

        // Act
        OAuth2User result = customOAuth2UserService.loadUser(naverRequest);

        // Assert
        assertNotNull(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loadUser_UnsupportedProvider_ThrowsException() {
        // Arrange
        ClientRegistration googleRegistration = ClientRegistration.withRegistrationId("google")
                .clientId("google-client-id")
                .clientSecret("google-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/google")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .clientName("Google")
                .build();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "google-access-token",
                java.time.Instant.now(),
                java.time.Instant.now().plusSeconds(3600));

        OAuth2UserRequest googleRequest = new OAuth2UserRequest(googleRegistration, accessToken);

        Map<String, Object> attributes = Map.of("email", "google@example.com");
        OAuth2User googleUser = new DefaultOAuth2User(Collections.emptySet(), attributes, "email");

        doReturn(googleUser).when(customOAuth2UserService).loadUserDelegate(any(OAuth2UserRequest.class));

        // Act & Assert
        assertThrows(OAuth2AuthenticationException.class, () -> customOAuth2UserService.loadUser(googleRequest));
    }
}
