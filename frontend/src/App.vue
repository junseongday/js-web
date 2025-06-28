<script setup lang="ts">
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { onMounted, ref, provide } from 'vue'
import Toast from '@/components/Toast.vue'
import '@/assets/app.css'

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
