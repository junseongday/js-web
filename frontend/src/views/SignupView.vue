<template>
  <div class="auth-container">
    <div class="auth-form">
      <h1>회원가입</h1>
      <form @submit.prevent="handleSignup">
        <div class="form-group">
          <label for="email">이메일</label>
          <input type="email" id="email" v-model="email" required />
        </div>
        <div class="form-group">
          <label for="nickname">닉네임</label>
          <input type="text" id="nickname" v-model="nickname" required />
        </div>
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input type="password" id="password" v-model="password" required />
        </div>
        <button type="submit" class="btn-submit">회원가입</button>
        <p v-if="error" class="error-message">{{ error }}</p>
      </form>
      <div class="switch-auth">
        이미 계정이 있으신가요? <RouterLink to="/login">로그인</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { signup } from '@/api'
import '@/assets/styles/auth.css'

const email = ref('')
const nickname = ref('')
const password = ref('')
const error = ref<string | null>(null)
const router = useRouter()

const handleSignup = async () => {
  try {
    await signup({
      email: email.value,
      nickname: nickname.value,
      password: password.value
    })
    router.push('/login')
  } catch (err) {
    error.value = '회원가입에 실패했습니다. 다시 시도해주세요.'
  }
}
</script> 