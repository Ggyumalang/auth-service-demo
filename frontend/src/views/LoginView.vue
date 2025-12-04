<script setup>
import { ref } from 'vue'
import axios from '@/api/axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')
const error = ref('')

const handleLogin = async () => {
  try {
    const response = await axios.post('/api/auth/login', {
      username: username.value,
      password: password.value
    })
    
    // Store token if response contains it (assuming JWT)
    if (response.data && response.data.accessToken) {
      localStorage.setItem('accessToken', response.data.accessToken)
    }
    
    router.push('/home')
  } catch (err) {
    error.value = 'Invalid username or password'
  }
}

const loginWithKakao = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/kakao'
}

const loginWithNaver = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/naver'
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <h1>Welcome Back</h1>
      <p class="subtitle">Sign in to continue</p>
      
      <form @submit.prevent="handleLogin" class="login-form">
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
            v-model="password" 
            type="password" 
            placeholder="Password" 
            required
          />
        </div>
        
        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <button type="submit" class="submit-btn">
          Sign In
        </button>
      </form>

      <div class="divider">
        <span>OR</span>
      </div>

      <div class="button-group">
        <button class="kakao-btn" @click="loginWithKakao">
          <span class="icon">💬</span> Login with Kakao
        </button>
        <button class="naver-btn" @click="loginWithNaver">
          <span class="icon">N</span> Login with Naver
        </button>
      </div>

      <div class="signup-link">
        Don't have an account? 
        <router-link to="/signup">Sign Up</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
}

.login-card {
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

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 1.5rem;
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
  margin-top: 0.5rem;
  transition: transform 0.2s;
}

.submit-btn:hover {
  transform: translateY(-2px);
}

.divider {
  display: flex;
  align-items: center;
  margin: 1.5rem 0;
  color: #666;
  font-size: 0.8rem;
}

.divider::before,
.divider::after {
  content: "";
  flex: 1;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.divider span {
  padding: 0 10px;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.kakao-btn {
  background-color: var(--color-kakao);
  color: var(--color-kakao-text);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.naver-btn {
  background-color: var(--color-naver);
  color: var(--color-naver-text);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.icon {
  font-weight: bold;
  font-size: 1.2rem;
}

.error-message {
  color: #ff4d4d;
  font-size: 0.9rem;
}

.signup-link {
  margin-top: 1.5rem;
  color: #888;
  font-size: 0.9rem;
}

.signup-link a {
  color: #4a90e2;
  text-decoration: none;
  font-weight: 600;
}

.signup-link a:hover {
  text-decoration: underline;
}
</style>
