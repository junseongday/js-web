<template>
  <div class="oauth-redirect">
    <div class="loading-container">
      <div class="spinner"></div>
      <p>{{ message }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, inject } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getMe } from '@/api'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const showToast = inject('showToast') as (message: string, type?: 'success' | 'error' | 'info' | 'warning') => void

const message = ref('로그인 처리 중...')

onMounted(async () => {
  try {
    const token = route.query.token as string
    const provider = route.query.provider as string
    
    if (!token) {
      message.value = '토큰이 없습니다.'
      showToast('로그인에 실패했습니다.', 'error')
      setTimeout(() => {
        router.push('/login')
      }, 2000)
      return
    }

    // 토큰 저장
    localStorage.setItem('token', token)
    authStore.token = token

    // 사용자 정보 가져오기
    const email = authStore.getEmailFromToken(token)
    if (email) {
      localStorage.setItem('email', email)
      await authStore.fetchUser()
      
      // 성공 메시지 표시
      message.value = `${provider} 로그인에 성공했습니다!`
      showToast(`${provider} 로그인에 성공했습니다!`, 'success')
      
      // 홈으로 리다이렉트
      setTimeout(() => {
        router.push('/')
      }, 1500)
    } else {
      throw new Error('토큰에서 이메일을 추출할 수 없습니다.')
    }
  } catch (error) {
    console.error('OAuth 리다이렉트 오류:', error)
    message.value = '로그인 처리 중 오류가 발생했습니다.'
    showToast('로그인 처리 중 오류가 발생했습니다.', 'error')
    
    // 로그인 페이지로 리다이렉트
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  }
})
</script>

<style scoped>
.oauth-redirect {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.loading-container {
  text-align: center;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

p {
  margin: 0;
  color: #333;
  font-size: 1.1rem;
}
</style> 