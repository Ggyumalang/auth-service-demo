package com.example.auth.exception;

import com.example.auth.crypto.exception.AesDecryptionException;
import com.example.auth.crypto.exception.CryptoException;
import com.example.auth.crypto.exception.InvalidEncryptedRequestException;
import com.example.auth.crypto.exception.RsaDecryptionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        log.warn("User already exists: {}", e.getMessage());
        return createErrorResponse(ErrorCode.USER_ALREADY_EXISTS, e.getMessage());
    }

    @ExceptionHandler(RsaDecryptionException.class)
    public ResponseEntity<ErrorResponse> handleRsaDecryptionException(RsaDecryptionException e) {
        log.error("RSA decryption failed: {}", e.getMessage(), e);
        return createErrorResponse(ErrorCode.RSA_DECRYPTION_FAILED);
    }

    @ExceptionHandler(AesDecryptionException.class)
    public ResponseEntity<ErrorResponse> handleAesDecryptionException(AesDecryptionException e) {
        log.error("AES decryption failed: {}", e.getMessage(), e);
        return createErrorResponse(ErrorCode.AES_DECRYPTION_FAILED);
    }

    @ExceptionHandler(InvalidEncryptedRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEncryptedRequestException(InvalidEncryptedRequestException e) {
        log.warn("Invalid encrypted request: {}", e.getMessage());
        return createErrorResponse(ErrorCode.INVALID_ENCRYPTED_REQUEST);
    }

    @ExceptionHandler(CryptoException.class)
    public ResponseEntity<ErrorResponse> handleCryptoException(CryptoException e) {
        log.error("Crypto exception: {}", e.getMessage(), e);
        return createErrorResponse(ErrorCode.DECRYPTION_FAILED, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception: {}", e.getMessage(), e);
        return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode) {
        String message = messageSource.getMessage(errorCode.getMessageCode(), null, LocaleContextHolder.getLocale());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode, message));
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode, String customMessage) {
        // 커스텀 메시지가 있으면 그것을 사용, 없으면 프로퍼티 메시지 사용
        String message = (customMessage != null && !customMessage.isEmpty()) ? customMessage
                : messageSource.getMessage(errorCode.getMessageCode(), null, LocaleContextHolder.getLocale());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode, message));
    }
}
