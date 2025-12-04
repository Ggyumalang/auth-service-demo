package com.example.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.internal_server_error"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "error.bad_request"),

    // Auth
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "error.auth.user_already_exists"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "error.auth.email_already_exists"),

    // Crypto
    DECRYPTION_FAILED(HttpStatus.BAD_REQUEST, "error.crypto.decryption_failed"),
    RSA_DECRYPTION_FAILED(HttpStatus.BAD_REQUEST, "error.crypto.rsa_decryption_failed"),
    AES_DECRYPTION_FAILED(HttpStatus.BAD_REQUEST, "error.crypto.aes_decryption_failed"),
    INVALID_ENCRYPTED_REQUEST(HttpStatus.BAD_REQUEST, "error.crypto.invalid_request");

    private final HttpStatus status;
    private final String messageCode;
}
