<script setup lang="ts">
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { onMounted, ref, provide } from 'vue'
import Toast from '@/components/Toast.vue'

const authStore = useAuthStore()
const router = useRouter()

// 토스트 상태 관리
const toast = ref({
  show: false,
  message: '',
  type: 'info' as 'success' | 'error' | 'info' | 'warning'
})

const showToast = (message: string, type: 'success' | 'error' | 'info' | 'warning' = 'info') => {
  toast.value = {
    show: true,
    message,
    type
  }
  
  setTimeout(() => {
    toast.value.show = false
  }, 3000)
}

// 전역으로 토스트 함수 제공
provide('showToast', showToast)

const logout = () => {
  authStore.logout()
  showToast('로그아웃되었습니다.', 'success')
  router.push('/login')
}

onMounted(() => {
  authStore.initialize()
})
</script>

<template>
  <div id="app-container">
    <header class="app-header">
      <div class="wrapper">
        <nav>
          <RouterLink to="/" class="logo">게시판</RouterLink>
          <div class="nav-links">
            <RouterLink to="/posts">게시물</RouterLink>
            <span v-if="authStore.isAuthenticated" class="user-info">
              <span>{{ authStore.currentUser?.nickname }}님</span>
              <button @click="logout" class="logout-button">로그아웃</button>
            </span>
            <div v-else class="guest-links">
              <RouterLink to="/login">로그인</RouterLink>
              <RouterLink to="/signup">회원가입</RouterLink>
            </div>
          </div>
        </nav>
      </div>
    </header>

    <main class="app-main">
      <RouterView />
    </main>

    <!-- 토스트 알림 -->
    <Toast 
      :show="toast.show" 
      :message="toast.message" 
      :type="toast.type" 
    />
  </div>
</template>

<style scoped>
#app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-header {
  background-color: #fff;
  border-bottom: 1px solid #e0e0e0;
  padding: 0 2rem;
}

.wrapper {
  max-width: 1280px;
  margin: 0 auto;
}

nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.logo {
  font-weight: bold;
  font-size: 1.5rem;
  color: #333;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.nav-links a {
  color: #555;
  font-weight: 500;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.guest-links {
  display: flex;
  gap: 1.5rem;
}

.logout-button {
    background-color: transparent;
    color: #555;
    border: 1px solid #ccc;
    padding: 6px 12px;
}

.logout-button:hover {
    background-color: #f5f5f5;
}

.app-main {
  flex-grow: 1;
  padding: 2rem 0;
}
</style>
