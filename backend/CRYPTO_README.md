# 🔐 암호화 시스템 문서 (Crypto System Documentation)

이 문서는 프로젝트에 적용된 **RSA-OAEP + AES-GCM 하이브리드 암호화 시스템**의 구조와 동작 원리를 설명합니다.

## 1. 개요 (Overview)

클라이언트와 서버 간의 민감한 데이터(예: 회원가입 정보)를 보호하기 위해 두 가지 암호화 방식을 결합하여 사용합니다.

- **RSA-OAEP (SHA-256)**: AES 대칭키를 안전하게 교환하기 위해 사용 (비대칭키 암호화)
- **AES-GCM (256-bit)**: 실제 대용량 데이터를 암호화하기 위해 사용 (대칭키 암호화)

## 2. 아키텍처 (Architecture)

Spring Security Filter Chain 내에서 `DecryptionFilter`가 가장 먼저 실행되어 암호화된 요청을 투명하게 처리합니다.

### Security Filter Chain 구조
```mermaid
graph TD
    Client[Client] -->|Encrypted Request| DecryptionFilter
    
    subgraph Spring Security Filter Chain
        DecryptionFilter[DecryptionFilter (Priority 1)]
        LoginFilter[GenerateTokenForUserFilter (Priority 2)]
        JwtFilter[JwtAuthenticationFilter (Priority 3)]
        Controller[Controller]
    end

    DecryptionFilter -->|Decrypts Request| DecryptionFilter
    DecryptionFilter -->|Decrypted Request| LoginFilter
    LoginFilter -->|Login Request| Client
    LoginFilter -->|Other Requests| JwtFilter
    JwtFilter --> Controller
```

> 상세한 시각화는 `docs/security_architecture.puml` 파일을 참고하세요.

## 3. 주요 컴포넌트 (Components)

### 🖥️ Backend (Java/Spring Boot)

| 컴포넌트 | 역할 |
|---|---|
| **`DecryptionFilter`** | HTTP 요청을 가로채서 암호화된 바디를 복호화하고, `DecryptedRequestWrapper`로 감싸서 다음 필터로 전달합니다. |
| **`CryptoUtils`** | RSA 및 AES 암호화/복호화 로직을 담당하는 유틸리티 클래스입니다. |
| **`RsaKeyService`** | RSA 키 쌍을 생성하고 Redis에 저장/조회하며, 주기적인 키 로테이션을 관리합니다. |
| **`CryptoController`** | 클라이언트에게 RSA 공개키를 제공하는 API 엔드포인트(`GET /api/crypto/public-key`)를 제공합니다. |

### 🌐 Frontend (Vue.js)

| 컴포넌트 | 역할 |
|---|---|
| **`EncryptionService`** | (`utils/encryption.js`) 서버에서 공개키를 가져오고, 데이터를 암호화하는 싱글톤 서비스입니다. |
| **`SignupView.vue`** | 회원가입 시 `EncryptionService`를 사용하여 데이터를 암호화한 후 전송합니다. |

## 4. 암호화 프로세스 (Encryption Flow)

1. **공개키 요청**: 클라이언트가 서버(`CryptoController`)에 RSA 공개키를 요청합니다.
2. **키 생성**: 클라이언트가 일회용 **AES-256 키**를 생성합니다.
3. **데이터 암호화**:
   - 데이터(JSON)를 **AES-GCM**으로 암호화합니다. (결과: `encryptedData`)
   - 생성한 AES 키를 **RSA-OAEP**로 암호화합니다. (결과: `encryptedKey`)
4. **전송**: `encryptedData`와 `encryptedKey`를 서버로 전송합니다.
5. **복호화 (서버)**:
   - `DecryptionFilter`가 요청을 가로챕니다.
   - 개인키로 `encryptedKey`를 복호화하여 **AES 키**를 얻습니다.
   - 복원된 AES 키로 `encryptedData`를 복호화하여 **원본 데이터**를 얻습니다.

## 5. 예외 처리 (Exception Handling)

명확한 에러 처리를 위해 커스텀 예외를 사용하며, 클라이언트에게 한글 에러 메시지를 반환합니다.

- **`RsaDecryptionException`**: RSA 키 복호화 실패 (파라미터 불일치 등)
- **`AesDecryptionException`**: 데이터 복호화 실패 (키 불일치, 변조 등)
- **`InvalidEncryptedRequestException`**: 잘못된 요청 형식

### 에러 응답 예시
```json
{
  "error": "암호화된 요청 처리 실패",
  "message": "AES 키 복호화에 실패했습니다. RSA 암호화 파라미터를 확인하세요."
}
```