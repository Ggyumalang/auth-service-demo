<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/api/axios'
import { useRouter } from 'vue-router'

const user = ref(null)
const loading = ref(true)
const router = useRouter()

onMounted(async () => {
  try {
    const response = await axios.get('/api/user')
    user.value = response.data
  } catch (error) {
    console.error('Failed to fetch user', error)
    router.push('/')
  } finally {
    loading.value = false
  }
})

const logout = async () => {
  try {
    await axios.post('/api/auth/logout')
  } catch (error) {
    console.error('Logout failed', error)
  } finally {
    localStorage.removeItem('accessToken')
    router.push('/')
  }
}
</script>

<template>
  <div class="home-container">
    <div v-if="loading" class="loading">Loading...</div>
    <div v-else-if="user" class="user-card">
      <div class="avatar-placeholder">{{ user.name ? user.name[0] : 'U' }}</div>
      <h2>Hello, {{ user.name || 'User' }}!</h2>
      <div class="user-details">
        <p v-for="(value, key) in user" :key="key">
          <strong>{{ key }}:</strong> {{ value }}
        </p>
      </div>
      <button class="logout-btn" @click="logout">Logout</button>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
}

.user-card {
  background: rgba(255, 255, 255, 0.05);
  padding: 3rem;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
  max-width: 600px;
  width: 100%;
}

.avatar-placeholder {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: bold;
  margin: 0 auto 1.5rem;
}

.user-details {
  text-align: left;
  background: rgba(0, 0, 0, 0.2);
  padding: 1.5rem;
  border-radius: 12px;
  margin: 2rem 0;
  overflow-wrap: break-word;
}

.logout-btn {
  background-color: #ff4757;
  color: white;
}
</style>
