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
import '@/assets/styles/auth.css'

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