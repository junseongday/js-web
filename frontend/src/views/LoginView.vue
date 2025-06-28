<template>
  <div class="auth-container">
    <div class="auth-form">
      <h1>로그인</h1>

      <!-- OAuth 로그인 버튼들 -->
      <div class="oauth-section">
        <p class="oauth-title">소셜 계정으로 로그인</p>
        <div class="oauth-buttons">
          <button
            @click="handleOAuthLogin('google')"
            class="oauth-btn google-btn"
            :disabled="loading || !oauthUrls?.google"
            :title="!oauthUrls?.google ? 'Google 로그인이 설정되지 않았습니다' : ''"
          >
            <span class="oauth-icon">G</span>
            Google로 로그인
          </button>
          <button
            @click="handleOAuthLogin('kakao')"
            class="oauth-btn kakao-btn"
            :disabled="loading || !oauthUrls?.kakao"
            :title="!oauthUrls?.kakao ? 'Kakao 로그인이 설정되지 않았습니다' : ''"
          >
            <span class="oauth-icon">K</span>
            Kakao로 로그인
          </button>
          <button
            @click="handleOAuthLogin('naver')"
            class="oauth-btn naver-btn"
            :disabled="loading || !oauthUrls?.naver"
            :title="!oauthUrls?.naver ? 'Naver 로그인이 설정되지 않았습니다' : ''"
          >
            <span class="oauth-icon">N</span>
            Naver로 로그인
          </button>
        </div>

        <!-- OAuth 설정 안내 메시지 -->
        <div v-if="!oauthUrls || Object.keys(oauthUrls).length === 0" class="oauth-notice">
          <p>소셜 로그인을 사용하려면 OAuth 설정이 필요합니다.</p>
          <p>관리자에게 문의하거나 OAUTH_SETUP.md 파일을 참조하세요.</p>
        </div>
      </div>

      <div class="divider">
        <span>또는</span>
      </div>

      <!-- 기존 로그인 폼 -->
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="email">이메일</label>
          <input type="email" id="email" v-model="email" required />
        </div>
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input type="password" id="password" v-model="password" required />
        </div>
        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? '로그인 중...' : '로그인' }}
        </button>
        <p v-if="error" class="error-message">{{ error }}</p>
      </form>

      <div class="switch-auth">
        계정이 없으신가요? <RouterLink to="/signup">회원가입</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, RouterLink, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const email = ref('')
const password = ref('')
const error = ref<string | null>(null)
const loading = ref(false)
const oauthUrls = ref<any>(null)
const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

onMounted(async () => {
  // URL 파라미터에서 OAuth 오류 확인
  const urlError = route.query.error as string
  const urlMessage = route.query.message as string

  if (urlError === 'oauth_failed') {
    error.value = `소셜 로그인에 실패했습니다: ${urlMessage || '알 수 없는 오류가 발생했습니다.'}`
    // URL에서 오류 파라미터 제거
    router.replace({ query: {} })
  }

  try {
    oauthUrls.value = await authStore.getOAuthLoginUrls()
    console.log('oauthUrls:', oauthUrls.value)
  } catch (err) {
    console.error('Failed to load OAuth URLs:', err)
    error.value = '소셜 로그인을 불러올 수 없습니다.'
  }
})

const handleLogin = async () => {
  if (loading.value) return

  loading.value = true
  error.value = null

  try {
    await authStore.login({ email: email.value, password: password.value })
    router.push('/')
  } catch (err) {
    error.value = '로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.'
  } finally {
    loading.value = false
  }
}

const handleOAuthLogin = (provider: string) => {
  if (loading.value || !oauthUrls.value) return

  const url = oauthUrls.value[provider]
  console.log('url:', url)
  if (!url) {
    error.value = `${provider} 로그인이 현재 설정되지 않았습니다. 관리자에게 문의하세요.`
    return
  }

  loading.value = true
  error.value = null

  try {
    window.location.href = url
  } catch (err) {
    error.value = `${provider} 로그인 중 오류가 발생했습니다.`
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.auth-form {
  max-width: 400px;
  width: 100%;
  padding: 2rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #333;
}

.oauth-section {
  margin-bottom: 1.5rem;
}

.oauth-title {
  text-align: center;
  margin-bottom: 1rem;
  color: #666;
  font-size: 0.9rem;
}

.oauth-buttons {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.oauth-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.oauth-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.oauth-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.google-btn {
  color: #4285f4;
  border-color: #4285f4;
}

.kakao-btn {
  color: #fee500;
  border-color: #fee500;
  background-color: #fee500;
  color: #000;
}

.naver-btn {
  color: #03c75a;
  border-color: #03c75a;
}

.oauth-icon {
  font-weight: bold;
  font-size: 1.1rem;
}

.divider {
  text-align: center;
  margin: 1.5rem 0;
  position: relative;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #e0e0e0;
}

.divider span {
  background: white;
  padding: 0 1rem;
  color: #666;
  font-size: 0.9rem;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

input:focus {
  outline: none;
  border-color: #4285f4;
  box-shadow: 0 0 0 2px rgba(66, 133, 244, 0.1);
}

.btn-submit {
  width: 100%;
  padding: 12px;
  background-color: #4285f4;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-submit:hover:not(:disabled) {
  background-color: #3367d6;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  color: #d32f2f;
  margin-top: 1rem;
  text-align: center;
  font-size: 0.9rem;
}

.switch-auth {
  margin-top: 1.5rem;
  text-align: center;
  color: #666;
  font-size: 0.9rem;
}

.switch-auth a {
  color: #4285f4;
  text-decoration: none;
}

.switch-auth a:hover {
  text-decoration: underline;
}

.oauth-notice {
  margin-top: 1rem;
  padding: 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  background-color: #f9f9f9;
}
</style>
