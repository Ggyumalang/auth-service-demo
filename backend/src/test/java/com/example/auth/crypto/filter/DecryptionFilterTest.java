package com.example.auth.crypto.filter;

import com.example.auth.crypto.dto.EncryptedRequestDto;
import com.example.auth.crypto.service.RsaKeyService;
import com.example.auth.crypto.util.CryptoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecryptionFilterTest {

    @Mock
    private RsaKeyService rsaKeyService;

    @Mock
    private CryptoUtils cryptoUtils;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private DecryptionFilter decryptionFilter;

    @Test
    @DisplayName("암호화된 요청 복호화 테스트")
    void doFilterInternal_ShouldDecryptEncryptedRequest() throws Exception {
        // given
        String encryptedBody = "{\"encryptedData\":\"encData\",\"encryptedKey\":\"encKey\"}";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("application/json");
        request.setContent(encryptedBody.getBytes(StandardCharsets.UTF_8));

        MockHttpServletResponse response = new MockHttpServletResponse();

        EncryptedRequestDto dto = new EncryptedRequestDto("encData", "encKey");
        when(objectMapper.readValue(any(byte[].class), eq(EncryptedRequestDto.class))).thenReturn(dto);
        when(rsaKeyService.getPrivateKey()).thenReturn("privateKey");
        when(cryptoUtils.decryptAesKey("encKey", "privateKey")).thenReturn("aesKey");
        when(cryptoUtils.decryptData("encData", "aesKey")).thenReturn("{\"username\":\"test\"}");

        // when
        decryptionFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(argThat(req -> {
            try {
                HttpServletRequest wrappedRequest = (HttpServletRequest) req;
                byte[] content = wrappedRequest.getInputStream().readAllBytes();
                String body = new String(content, StandardCharsets.UTF_8);
                return body.equals("{\"username\":\"test\"}");
            } catch (Exception e) {
                return false;
            }
        }), eq(response));
    }

    @Test
    @DisplayName("일반 요청은 그대로 통과")
    void doFilterInternal_ShouldPassThroughNormalRequest() throws Exception {
        // given
        String normalBody = "{\"username\":\"test\"}";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("application/json");
        request.setContent(normalBody.getBytes(StandardCharsets.UTF_8));

        MockHttpServletResponse response = new MockHttpServletResponse();

        // EncryptedRequestDto로 파싱은 시도하지만, 필드가 null임
        when(objectMapper.readValue(any(byte[].class), eq(EncryptedRequestDto.class)))
                .thenReturn(new EncryptedRequestDto());

        // when
        decryptionFilter.doFilter(request, response, filterChain);

        // then
        verify(cryptoUtils, never()).decryptAesKey(anyString(), anyString());
        verify(filterChain).doFilter(any(HttpServletRequest.class), eq(response));
    }
}
