<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <div class="failure-container">
    <h2>Login Failed</h2>
    <p>Something went wrong during authentication.</p>
    <button @click="router.push('/')">Try Again</button>
  </div>
</template>

<style scoped>
.failure-container {
  text-align: center;
  padding-top: 5rem;
  color: #ff6b6b;
}
</style>
