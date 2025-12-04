package com.example.auth.crypto.controller;

import com.example.auth.crypto.service.RsaKeyService;
import com.example.auth.crypto.util.CryptoUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoTestController {

    private final RsaKeyService rsaKeyService;
    private final CryptoUtils cryptoUtils;

    @PostMapping("/test-decrypt")
    @Operation(summary = "RSA 복호화 테스트")
    public ResponseEntity<?> testDecrypt(@RequestBody Map<String, String> request) {
        try {
            String encryptedKey = request.get("encryptedKey");
            log.info("Testing RSA decryption...");
            log.info("Encrypted key (Base64): {}", encryptedKey);
            log.info("Encrypted key length: {}", encryptedKey.length());

            String privateKey = rsaKeyService.getPrivateKey();
            log.info("Private key available: {}", privateKey != null);

            String decryptedKey = cryptoUtils.decryptAesKey(encryptedKey, privateKey);
            log.info("Decryption successful!");
            log.info("Decrypted value: {}", decryptedKey);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "decryptedKey", decryptedKey,
                    "decryptedLength", decryptedKey.length()));
        } catch (Exception e) {
            log.error("Decryption test failed", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "type", e.getClass().getSimpleName()));
        }
    }
}
