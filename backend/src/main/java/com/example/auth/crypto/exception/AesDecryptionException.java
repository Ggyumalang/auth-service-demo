package com.example.auth.crypto.exception;

/**
 * AES 데이터 복호화 실패 예외
 */
public class AesDecryptionException extends CryptoException {

    public AesDecryptionException(String message) {
        super(message);
    }

    public AesDecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
