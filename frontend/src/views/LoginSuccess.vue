<script setup>
import { onMounted } from 'vue'
import {useRoute, useRouter} from 'vue-router'

const router = useRouter()
const route = useRoute();
const token = String(route.query.token || "");

onMounted(() => {
  if(token) {
    localStorage.setItem('accessToken', token);
  }

  setTimeout(() => {
    router.push('/home')
  }, 500)
})
</script>

<template>
  <div class="success-container">
    <h2>Login Successful!</h2>
    <p>Redirecting you...</p>
  </div>
</template>

<style scoped>
.success-container {
  text-align: center;
  padding-top: 5rem;
}
</style>
