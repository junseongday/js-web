<template>
  <div class="auth-container">
    <div class="auth-form">
      <h1>로그인</h1>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="email">이메일</label>
          <input type="email" id="email" v-model="email" required />
        </div>
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input type="password" id="password" v-model="password" required />
        </div>
        <button type="submit" class="btn-submit">로그인</button>
        <p v-if="error" class="error-message">{{ error }}</p>
      </form>
      <div class="switch-auth">
        계정이 없으신가요? <RouterLink to="/signup">회원가입</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const email = ref('')
const password = ref('')
const error = ref<string | null>(null)
const authStore = useAuthStore()
const router = useRouter()

const handleLogin = async () => {
  try {
    await authStore.login({ email: email.value, password: password.value })
    router.push('/')
  } catch (err) {
    error.value = '로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.'
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
}
.auth-form {
  max-width: 400px;
  width: 100%;
  padding: 2rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #fff;
}
h1 {
  text-align: center;
  margin-bottom: 1.5rem;
}
.form-group {
  margin-bottom: 1rem;
}
label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}
.btn-submit {
  width: 100%;
  padding: 12px;
}
.error-message {
  color: red;
  margin-top: 1rem;
  text-align: center;
}
.switch-auth {
  margin-top: 1.5rem;
  text-align: center;
  color: #555;
}
</style> 