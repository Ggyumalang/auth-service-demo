package com.example.auth.controller;

import com.example.auth.crypto.dto.EncryptedRequestDto;
import com.example.auth.crypto.service.RsaKeyService;
import com.example.auth.dto.SignupRequest;
import com.example.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerEncryptionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private RsaKeyService rsaKeyService;

    @Test
    @DisplayName("회원가입 암호화 요청 테스트")
    void signup_EncryptedRequest_ShouldBeDecryptedAndProcessed() throws Exception {
        // 1. RSA 키 쌍 생성 (테스트용)
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        String privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        // RsaKeyService가 테스트용 개인키를 반환하도록 설정
        when(rsaKeyService.getPrivateKey()).thenReturn(privateKeyBase64);

        // 2. 데이터 준비 (평문)
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("encryptedUser");
        signupRequest.setPassword("password123");
        signupRequest.setEmail("encrypted@test.com");
        String plainJson = objectMapper.writeValueAsString(signupRequest);

        // 3. 암호화 수행 (클라이언트 동작 시뮬레이션)
        // 3-1. 일회용 AES 키 생성
        String aesKey = "1234567890123456"; // 16 bytes

        // 3-2. 데이터 암호화 (AES-GCM)
        byte[] iv = new byte[12]; // 96 bits for GCM
        new java.security.SecureRandom().nextBytes(iv);

        SecretKeySpec secretKey = new SecretKeySpec(aesKey.getBytes(), "AES");
        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
        byte[] ciphertextWithTag = aesCipher.doFinal(plainJson.getBytes());

        // Combine IV + ciphertext + tag
        byte[] combined = new byte[12 + ciphertextWithTag.length];
        System.arraycopy(iv, 0, combined, 0, 12);
        System.arraycopy(ciphertextWithTag, 0, combined, 12, ciphertextWithTag.length);
        String encryptedData = Base64.getEncoder().encodeToString(combined);

        // 3-3. AES 키 암호화 (RSA-OAEP/SHA-256)
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT);

        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic(), oaepParams);

        // Frontend sends Base64 encoded AES key, encrypted with RSA
        String aesKeyBase64 = Base64.getEncoder().encodeToString(aesKey.getBytes());
        byte[] encryptedKeyBytes = rsaCipher.doFinal(aesKeyBase64.getBytes());
        String encryptedKey = Base64.getEncoder().encodeToString(encryptedKeyBytes);

        // 4. 요청 DTO 생성
        EncryptedRequestDto encryptedRequest = new EncryptedRequestDto(encryptedData, encryptedKey);

        // 5. 요청 전송
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Encrypted-Body", "true")
                .content(objectMapper.writeValueAsString(encryptedRequest)))
                .andExpect(status().isOk());

        // 6. 검증: AuthService가 복호화된 데이터로 호출되었는지 확인
        verify(authService).signup(any(SignupRequest.class));
    }

    @Test
    @DisplayName("회원가입 평문 요청 테스트 (필터 통과 확인)")
    void signup_PlainRequest_ShouldBeProcessedNormally() throws Exception {
        // 1. 데이터 준비 (평문)
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("plainUser");
        signupRequest.setPassword("password123");
        signupRequest.setEmail("plain@test.com");

        // 2. 요청 전송
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        // 3. 검증: AuthService가 호출되었는지 확인
        verify(authService).signup(any(SignupRequest.class));
    }
}
