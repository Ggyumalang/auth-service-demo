package com.example.auth.crypto.exception;

/**
 * 암호화 관련 예외의 기본 클래스
 */
public class CryptoException extends RuntimeException {

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
