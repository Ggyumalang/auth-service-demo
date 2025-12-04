package com.example.auth.crypto.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 암호화된 요청 데이터 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EncryptedRequestDto {

    /**
     * AES 키로 암호화된 실제 데이터 (Base64)
     */
    private String encryptedData;

    /**
     * RSA 공개키로 암호화된 AES 키 (Base64)
     */
    private String encryptedKey;
}
