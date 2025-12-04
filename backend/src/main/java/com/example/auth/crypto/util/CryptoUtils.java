package com.example.auth.crypto.util;

import com.example.auth.crypto.exception.AesDecryptionException;
import com.example.auth.crypto.exception.RsaDecryptionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * 암호화 유틸리티
 * - RSA-OAEP with SHA-256 for key exchange
 * - AES-GCM for authenticated encryption
 */
@Slf4j
@Component
public class CryptoUtils {

    private static final int GCM_IV_LENGTH = 12; // 96 bits (recommended for GCM)
    private static final int GCM_TAG_LENGTH = 128; // 128 bits authentication tag

    /**
     * RSA-OAEP/SHA-256 개인키로 AES 키 복호화
     */
    public String decryptAesKey(String encryptedKeyBase64, String privateKeyBase64) {
        try {
            log.debug("RSA-OAEP/SHA-256으로 AES 키 복호화 시작");
            log.debug("암호화된 키 길이: {}", encryptedKeyBase64.length());

            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            // RSA-OAEP with SHA-256
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    MGF1ParameterSpec.SHA256,
                    PSource.PSpecified.DEFAULT);

            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedKeyBase64));
            String result = new String(decryptedBytes, StandardCharsets.UTF_8);

            log.debug("AES 키 복호화 성공, 길이: {}", result.length());
            return result;
        } catch (Exception e) {
            log.error("AES 키 복호화 실패", e);
            throw new RsaDecryptionException("AES 키 복호화에 실패했습니다. RSA 암호화 파라미터를 확인하세요.", e);
        }
    }

    /**
     * AES-GCM으로 데이터 복호화
     * 
     * @param encryptedDataBase64 Base64 encoded: IV (12 bytes) + Ciphertext + Auth
     *                            Tag (16 bytes)
     * @param aesKeyString        AES key as string (will be converted to bytes)
     */
    public String decryptData(String encryptedDataBase64, String aesKeyString) {
        try {
            // Decode the combined data (IV + ciphertext + tag)
            byte[] combined = Base64.getDecoder().decode(encryptedDataBase64);

            // Extract IV (first 12 bytes)
            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);

            // Extract ciphertext + tag (remaining bytes)
            byte[] ciphertextWithTag = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, GCM_IV_LENGTH, ciphertextWithTag, 0, ciphertextWithTag.length);

            // Decode Base64 AES key to bytes
            byte[] keyBytes = Base64.getDecoder().decode(aesKeyString);

            // Validate key length (should be 16, 24, or 32 bytes after Base64 decoding)
            if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
                throw new IllegalArgumentException(
                        "Invalid AES key length: " + keyBytes.length + " bytes. Expected 16, 24, or 32 bytes.");
            }

            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            // AES-GCM decryption
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            byte[] decryptedBytes = cipher.doFinal(ciphertextWithTag);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("데이터 복호화 실패", e);
            throw new AesDecryptionException("데이터 복호화에 실패했습니다. AES-GCM 파라미터 또는 인증 태그를 확인하세요.", e);
        }
    }
}
