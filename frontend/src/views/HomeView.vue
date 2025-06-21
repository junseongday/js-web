<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { RouterLink } from 'vue-router'

const authStore = useAuthStore()
</script>

<template>
  <div class="home-container">
    <div class="hero-section">
      <h1>게시판에 오신 것을 환영합니다!</h1>
      <p>자유롭게 생각을 공유하고, 다른 사람들과 소통하는 공간입니다.</p>
      <div class="actions">
        <RouterLink to="/posts" class="btn btn-primary">게시물 보러가기</RouterLink>
        <RouterLink v-if="!authStore.isAuthenticated" to="/signup" class="btn btn-secondary">회원가입</RouterLink>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  text-align: center;
  padding: 4rem 2rem;
}

.hero-section {
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  font-size: 3rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

p {
  font-size: 1.2rem;
  color: #555;
  margin-bottom: 2rem;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.btn {
  padding: 12px 24px;
  border-radius: 6px;
  font-weight: bold;
  text-decoration: none;
  transition: all 0.3s;
}

.btn-primary {
  background-color: #007bff;
  color: white;
  border: 1px solid #007bff;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.btn-secondary {
  background-color: transparent;
  color: #333;
  border: 1px solid #ccc;
}

.btn-secondary:hover {
  background-color: #f5f5f5;
}
</style>
