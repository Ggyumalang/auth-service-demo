<script setup>
import { ref } from 'vue'
import axios from '@/api/axios'
import { useRouter } from 'vue-router'
import { encryptionService } from '@/utils/encryption'

const router = useRouter()
const username = ref('')
const email = ref('')
const password = ref('')
const error = ref('')
const isLoading = ref(false)

const handleSignup = async () => {
  if (isLoading.value) return
  
  isLoading.value = true
  error.value = ''
  
  try {
    // 회원가입 데이터 준비
    const signupData = {
      username: username.value,
      email: email.value,
      password: password.value
    }

    // 데이터 암호화 (RSA-OAEP + AES-GCM)
    const encryptedPayload = await encryptionService.encryptData(signupData)
    
    // 암호화된 데이터로 API 호출
    await axios.post('/api/auth/signup', encryptedPayload, {
      headers: {
        'X-Encrypted-Body': 'true'
      }
    })
    
    // 성공 알림
    alert('✅ 회원가입이 완료되었습니다!')
    
    // 성공 시 홈으로 이동
    router.push('/')
  } catch (err) {
    console.error('Signup error:', err)
    
    // 에러 메시지 추출
    let errorMessage = 'Signup failed'
    
    if (err.response?.data?.message) {
      errorMessage = err.response.data.message
    } else if (err.response?.data) {
      errorMessage = typeof err.response.data === 'string' 
        ? err.response.data 
        : JSON.stringify(err.response.data)
    } else if (err.message) {
      errorMessage = err.message
    }
    
    // 실패 알림
    alert(`❌ 회원가입 실패\n\n${errorMessage}`)
    
    error.value = errorMessage
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="signup-container">
    <div class="signup-card">
      <h1>Create Account</h1>
      <p class="subtitle">Join us today</p>
      
      <form @submit.prevent="handleSignup" class="signup-form">
        <div class="form-group">
          <input 
            v-model="username" 
            type="text" 
            placeholder="Username" 
            required
          />
        </div>
        <div class="form-group">
          <input 
            v-model="email" 
            type="email" 
            placeholder="Email" 
            required
          />
        </div>
        <div class="form-group">
          <input 
            v-model="password" 
            type="password" 
            placeholder="Password" 
            required
          />
        </div>
        
        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <button type="submit" class="submit-btn" :disabled="isLoading">
          <span v-if="isLoading">🔐 암호화 중...</span>
          <span v-else>Sign Up</span>
        </button>
        
        <div class="encryption-info">
          🔒 RSA-OAEP + AES-GCM 암호화 적용
        </div>
      </form>

      <div class="login-link">
        Already have an account? 
        <router-link to="/">Login</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.signup-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
}

.signup-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  padding: 3rem;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: 400px;
  text-align: center;
}

h1 {
  margin-bottom: 0.5rem;
  font-size: 2rem;
  font-weight: 700;
  background: linear-gradient(to right, #fff, #aaa);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  color: #888;
  margin-bottom: 2rem;
}

.signup-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.2);
  color: white;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: var(--color-primary);
}

.submit-btn {
  background: linear-gradient(to right, #4a90e2, #357abd);
  color: white;
  padding: 12px;
  border-radius: 8px;
  font-weight: 600;
  margin-top: 1rem;
  transition: transform 0.2s;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.error-message {
  color: #ff4d4d;
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

.encryption-info {
  margin-top: 1rem;
  padding: 0.75rem;
  background: rgba(74, 144, 226, 0.1);
  border: 1px solid rgba(74, 144, 226, 0.3);
  border-radius: 6px;
  color: #4a90e2;
  font-size: 0.85rem;
  text-align: center;
}

.login-link {
  margin-top: 1.5rem;
  color: #888;
  font-size: 0.9rem;
}

.login-link a {
  color: #4a90e2;
  text-decoration: none;
  font-weight: 600;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
