<template>
  <div class="auth-container">
    <div class="auth-form">
      <h1>로그인</h1>

      <!-- OAuth 로그인 버튼들 -->
      <div v-if="isOAuthEnabled" class="oauth-section">
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

      <!-- OAuth가 비활성화된 경우 안내 메시지 -->
      <div v-else class="oauth-disabled-notice">
        <p>현재 소셜 로그인이 비활성화되어 있습니다.</p>
        <p>관리자에게 문의하세요.</p>
      </div>

      <div v-if="isOAuthEnabled" class="divider">
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
import { ref, onMounted, computed } from 'vue'
import { useRouter, RouterLink, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import '@/assets/styles/auth.css'

const email = ref('')
const password = ref('')
const error = ref<string | null>(null)
const loading = ref(false)
const oauthUrls = ref<any>(null)
const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

// OAuth 활성화 상태
const isOAuthEnabled = computed(() => authStore.isOAuthEnabled)

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
    // OAuth 상태 확인
    await authStore.getOAuthStatusInfo()
    
    // OAuth가 활성화된 경우에만 URL 가져오기
    if (authStore.isOAuthEnabled) {
      oauthUrls.value = await authStore.getOAuthLoginUrls()
      console.log('oauthUrls:', oauthUrls.value)
    }
  } catch (err) {
    console.error('Failed to load OAuth information:', err)
    error.value = '소셜 로그인 정보를 불러올 수 없습니다.'
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
