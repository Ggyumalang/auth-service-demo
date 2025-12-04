package com.example.auth.crypto.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class CryptoUtilsTest {

    private final CryptoUtils cryptoUtils = new CryptoUtils();
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    @Test
    @DisplayName("RSA-OAEP/SHA-256 암호화된 AES 키 복호화 테스트")
    void decryptAesKey_ShouldDecryptCorrectly() throws Exception {
        // 1. RSA 키 쌍 생성
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        String privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        // 2. AES 키 생성
        String originalAesKey = "1234567890123456"; // 16 bytes

        // 3. AES 키를 RSA-OAEP/SHA-256 공개키로 암호화
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT);

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic(), oaepParams);
        byte[] encryptedBytes = cipher.doFinal(originalAesKey.getBytes());
        String encryptedKeyBase64 = Base64.getEncoder().encodeToString(encryptedBytes);

        // 4. 복호화 테스트
        String decryptedAesKey = cryptoUtils.decryptAesKey(encryptedKeyBase64, privateKeyBase64);

        // 5. 검증
        assertThat(decryptedAesKey).isEqualTo(originalAesKey);
    }

    @Test
    @DisplayName("AES-GCM 암호화된 데이터 복호화 테스트")
    void decryptData_ShouldDecryptCorrectly() throws Exception {
        // 1. AES 키 준비
        String aesKey = "1234567890123456"; // 16 bytes

        // 2. 데이터 암호화 (AES-GCM)
        String originalData = "{\"username\":\"test\"}";

        // Generate random IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // Encrypt with AES-GCM
        SecretKeySpec secretKey = new SecretKeySpec(aesKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
        byte[] ciphertextWithTag = cipher.doFinal(originalData.getBytes());

        // Combine IV + ciphertext + tag
        byte[] combined = new byte[GCM_IV_LENGTH + ciphertextWithTag.length];
        System.arraycopy(iv, 0, combined, 0, GCM_IV_LENGTH);
        System.arraycopy(ciphertextWithTag, 0, combined, GCM_IV_LENGTH, ciphertextWithTag.length);

        String encryptedDataBase64 = Base64.getEncoder().encodeToString(combined);

        // 3. 복호화 테스트
        String decryptedData = cryptoUtils.decryptData(encryptedDataBase64, aesKey);

        // 4. 검증
        assertThat(decryptedData).isEqualTo(originalData);
    }
}
