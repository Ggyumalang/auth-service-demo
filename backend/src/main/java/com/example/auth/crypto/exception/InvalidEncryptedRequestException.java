package com.example.auth.crypto.exception;

/**
 * 잘못된 암호화 요청 예외
 */
public class InvalidEncryptedRequestException extends CryptoException {

    public InvalidEncryptedRequestException(String message) {
        super(message);
    }

    public InvalidEncryptedRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
