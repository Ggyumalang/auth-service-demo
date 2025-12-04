package com.example.auth.crypto.controller;

import com.example.auth.crypto.service.RsaKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
@Tag(name = "Crypto", description = "암호화 관련 API")
public class CryptoController {

    private final RsaKeyService rsaKeyService;

    @GetMapping("/public-key")
    @Operation(summary = "서버 RSA 공개키 조회", description = "클라이언트가 데이터를 암호화하기 위한 공개키를 반환합니다.")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        String publicKey = rsaKeyService.getPublicKey();
        if (publicKey == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(Map.of("publicKey", publicKey));
    }
}
