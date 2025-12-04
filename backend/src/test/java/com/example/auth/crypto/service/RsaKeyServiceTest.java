package com.example.auth.crypto.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RsaKeyServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RsaKeyService rsaKeyService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("RSA 키 쌍 생성 및 Redis 저장 테스트")
    void initKeys_ShouldGenerateAndStoreKeys() {
        // when
        rsaKeyService.initKeys();

        // then
        verify(valueOperations, times(2)).set(anyString(), anyString());
        verify(valueOperations).set(eq("server:rsa:public"), anyString());
        verify(valueOperations).set(eq("server:rsa:private"), anyString());
    }

    @Test
    @DisplayName("공개키 조회 테스트")
    void getPublicKey_ShouldReturnKeyFromRedis() {
        // given
        String expectedKey = "public-key-content";
        when(valueOperations.get("server:rsa:public")).thenReturn(expectedKey);

        // when
        String actualKey = rsaKeyService.getPublicKey();

        // then
        assertThat(actualKey).isEqualTo(expectedKey);
    }
}
