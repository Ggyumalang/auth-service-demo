package com.example.auth.crypto.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * RSA 키 관리 서비스
 * 서버 부팅 시 RSA 키 쌍을 생성하고 Redis에 저장합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RsaKeyService {

    private final StringRedisTemplate redisTemplate;

    private static final String PUBLIC_KEY = "server:rsa:public";
    private static final String PRIVATE_KEY = "server:rsa:private";
    private static final int KEY_SIZE = 2048;

    @PostConstruct
    public void initKeys() {
        try {
            log.info("Generating RSA Key Pair...");
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(KEY_SIZE);
            KeyPair keyPair = generator.generateKeyPair();

            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

            // Redis에 저장 (덮어쓰기)
            redisTemplate.opsForValue().set(PUBLIC_KEY, publicKey);
            redisTemplate.opsForValue().set(PRIVATE_KEY, privateKey);

            log.info("RSA Key Pair generated and stored in Redis.");

        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate RSA keys", e);
            throw new RuntimeException("RSA algorithm not supported", e);
        }
    }

    /**
     * 서버 공개키 조회 (Base64)
     */
    public String getPublicKey() {
        return redisTemplate.opsForValue().get(PUBLIC_KEY);
    }

    /**
     * 서버 개인키 조회 (Base64)
     */
    public String getPrivateKey() {
        return redisTemplate.opsForValue().get(PRIVATE_KEY);
    }
}
