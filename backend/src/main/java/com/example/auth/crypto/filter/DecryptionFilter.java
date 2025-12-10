package com.example.auth.crypto.filter;

import com.example.auth.crypto.dto.EncryptedRequestDto;
import com.example.auth.crypto.exception.AesDecryptionException;
import com.example.auth.crypto.exception.RsaDecryptionException;
import com.example.auth.crypto.service.RsaKeyService;
import com.example.auth.crypto.util.CryptoUtils;
import com.example.auth.crypto.wrapper.DecryptedRequestWrapper;
import com.example.auth.exception.ErrorCode;
import com.example.auth.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 복호화 필터
 * 암호화된 요청을 가로채서 복호화한 후 컨트롤러로 전달합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DecryptionFilter extends OncePerRequestFilter {

    private final RsaKeyService rsaKeyService;
    private final CryptoUtils cryptoUtils;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // POST, PUT 요청이고 JSON 컨텐츠인 경우에만 처리
        if (isEncryptedRequest(request)) {
            try {
                // 1. Request Body 읽기
                byte[] bodyBytes = StreamUtils.copyToByteArray(request.getInputStream());
                if (bodyBytes.length == 0) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // 2. EncryptedRequestDto로 파싱 시도
                EncryptedRequestDto encryptedDto = objectMapper.readValue(bodyBytes, EncryptedRequestDto.class);

                // 3. 암호화된 데이터가 있는지 확인
                if (encryptedDto.getEncryptedData() != null && encryptedDto.getEncryptedKey() != null) {
                    log.debug("Encrypted request detected. Decrypting...");

                    // 4. RSA 개인키로 AES 키 복호화
                    String privateKey = rsaKeyService.getPrivateKey();
                    String aesKey = cryptoUtils.decryptAesKey(encryptedDto.getEncryptedKey(), privateKey);

                    // 5. AES 키로 데이터 복호화
                    String decryptedJson = cryptoUtils.decryptData(encryptedDto.getEncryptedData(), aesKey);

                    log.debug("Decryption successful.");

                    // 6. 복호화된 데이터로 Request 래핑
                    DecryptedRequestWrapper wrappedRequest = new DecryptedRequestWrapper(request, decryptedJson);
                    filterChain.doFilter(wrappedRequest, response);
                    return;
                }

                // 암호화된 형식이 아니면 원본 바이트로 다시 래핑해서 전달 (이미 읽었으므로)
                filterChain.doFilter(
                        new DecryptedRequestWrapper(request, new String(bodyBytes, StandardCharsets.UTF_8)), response);

            } catch (RsaDecryptionException | AesDecryptionException e) {
                log.error("복호화 실패: {}", e.getMessage(), e);
                sendErrorResponse(response, ErrorCode.RSA_DECRYPTION_FAILED, e.getMessage());
                return;
            } catch (Exception e) {
                log.error("요청 복호화 중 예상치 못한 오류 발생", e);
                sendErrorResponse(response, ErrorCode.INTERNAL_SERVER_ERROR, "요청 처리 중 오류가 발생했습니다");
                return;
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private boolean isEncryptedRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String contentType = request.getContentType();
        String encryptedHeader = request.getHeader("X-Encrypted-Body");

        if (method == null) {
            return false;
        }

        return (HttpMethod.POST.matches(method) || HttpMethod.PUT.matches(method)) &&
                contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE) &&
                "true".equalsIgnoreCase(encryptedHeader);
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode, String detailMessage)
            throws IOException {
        if (response.isCommitted()) {
            log.error("응답이 이미 커밋되어 에러 응답을 전송할 수 없습니다");
            return;
        }

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().getReasonPhrase())
                .code(errorCode.name())
                .message(detailMessage)
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}
