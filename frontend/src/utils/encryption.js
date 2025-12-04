/**
 * 암호화 유틸리티
 * RSA-OAEP/SHA-256 + AES-GCM 하이브리드 암호화
 */

import axios from '@/api/axios'

class EncryptionService {
    constructor() {
        this.publicKey = null
    }

    /**
     * 서버로부터 RSA 공개키 가져오기
     */
    async fetchPublicKey() {
        if (this.publicKey) {
            return this.publicKey
        }

        try {
            const response = await axios.get('/api/crypto/public-key')
            const publicKeyBase64 = response.data.publicKey  // JSON 객체에서 publicKey 추출

            // Base64 디코딩
            const binaryDer = Uint8Array.from(atob(publicKeyBase64), c => c.charCodeAt(0))

            // RSA-OAEP 공개키 임포트
            this.publicKey = await crypto.subtle.importKey(
                'spki',
                binaryDer,
                {
                    name: 'RSA-OAEP',
                    hash: 'SHA-256'
                },
                false,
                ['encrypt']
            )

            return this.publicKey
        } catch (error) {
            console.error('Failed to fetch public key:', error)
            throw new Error('암호화 키를 가져오는데 실패했습니다.')
        }
    }

    /**
     * 데이터 암호화 (RSA-OAEP + AES-GCM)
     * @param {Object} data - 암호화할 데이터 객체
     * @returns {Promise<{encryptedData: string, encryptedKey: string}>}
     */
    async encryptData(data) {
        try {
            console.log('🔐 [Encryption] Starting...')
            console.log('📝 [Encryption] Original data:', data)

            // 1. 일회용 AES-256 키 생성
            const aesKey = crypto.getRandomValues(new Uint8Array(32))
            console.log('✅ [Encryption] Generated AES-256 key, length:', aesKey.length)

            // 2. AES-GCM으로 데이터 암호화
            const encryptedData = await this.encryptWithAES(data, aesKey)
            console.log('✅ [Encryption] Data encrypted with AES-GCM')

            // 3. AES 키를 Base64로 인코딩 (RSA 암호화 전)
            const aesKeyBase64 = this.arrayBufferToBase64(aesKey)
            console.log('✅ [Encryption] AES key as Base64:', aesKeyBase64)
            console.log('   Length:', aesKeyBase64.length)

            const encoder = new TextEncoder()
            const aesKeyBytes = encoder.encode(aesKeyBase64)
            console.log('✅ [Encryption] AES key as UTF-8 bytes, length:', aesKeyBytes.length)

            // 4. RSA-OAEP로 Base64 인코딩된 AES 키 암호화
            const publicKey = await this.fetchPublicKey()
            const encryptedKey = await crypto.subtle.encrypt(
                { name: 'RSA-OAEP' },
                publicKey,
                aesKeyBytes
            )
            console.log('✅ [Encryption] AES key encrypted with RSA-OAEP')

            const result = {
                encryptedData: this.arrayBufferToBase64(encryptedData),
                encryptedKey: this.arrayBufferToBase64(encryptedKey)
            }

            console.log('✅ [Encryption] Complete!')
            console.log('   Encrypted data length:', result.encryptedData.length)
            console.log('   Encrypted key length:', result.encryptedKey.length)
            console.log('📤 [Encryption] Sending to server:', result)

            return result
        } catch (error) {
            console.error('❌ [Encryption] Failed:', error)
            throw new Error('데이터 암호화에 실패했습니다.')
        }
    }

    /**
     * AES-GCM으로 데이터 암호화
     * @private
     */
    async encryptWithAES(data, aesKeyBytes) {
        // AES 키 임포트
        const aesKey = await crypto.subtle.importKey(
            'raw',
            aesKeyBytes,
            { name: 'AES-GCM', length: 256 },
            false,
            ['encrypt']
        )

        // IV 생성 (96 bits for GCM)
        const iv = crypto.getRandomValues(new Uint8Array(12))

        // 데이터 암호화
        const encoder = new TextEncoder()
        const dataBytes = encoder.encode(JSON.stringify(data))

        const ciphertextWithTag = await crypto.subtle.encrypt(
            { name: 'AES-GCM', iv },
            aesKey,
            dataBytes
        )

        // IV + ciphertext + tag 결합
        const combined = new Uint8Array(iv.length + ciphertextWithTag.byteLength)
        combined.set(iv, 0)
        combined.set(new Uint8Array(ciphertextWithTag), iv.length)

        return combined
    }

    /**
     * ArrayBuffer를 Base64로 변환
     * @private
     */
    arrayBufferToBase64(buffer) {
        const bytes = new Uint8Array(buffer)
        let binary = ''
        for (let i = 0; i < bytes.byteLength; i++) {
            binary += String.fromCharCode(bytes[i])
        }
        return btoa(binary)
    }
}

// 싱글톤 인스턴스 export
export const encryptionService = new EncryptionService()
