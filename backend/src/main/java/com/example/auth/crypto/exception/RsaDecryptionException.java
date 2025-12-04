package com.example.auth.crypto.exception;

/**
 * RSA 키 복호화 실패 예외
 */
public class RsaDecryptionException extends CryptoException {

    public RsaDecryptionException(String message) {
        super(message);
    }

    public RsaDecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
