import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getMe, getOAuthUrls } from '@/api'
import type { LoginRequest, User, OAuthUrls } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))

  const isAuthenticated = computed(() => !!token.value)
  const currentUser = computed(() => user.value)

  async function login(credentials: LoginRequest) {
    try {
      const { data } = await loginApi(credentials)
      token.value = data.token
      localStorage.setItem('token', data.token)
      localStorage.setItem('email', data.email)
      await fetchUser()
    } catch (error) {
      console.error('Login failed:', error)
      throw error
    }
  }

  async function getOAuthLoginUrls(): Promise<OAuthUrls> {
    try {
      const { data } = await getOAuthUrls()
      return data
    } catch (error) {
      console.error('Failed to get OAuth URLs:', error)
      throw error
    }
  }

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('email')
  }

  async function fetchUser() {
    if (!token.value) return
    try {
      const { data } = await getMe()
      user.value = data
    } catch (error) {
      console.error('Failed to fetch user:', error)
      logout()
    }
  }

  function getEmailFromToken(token: string): string | null {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload.sub || null
    } catch (error) {
      console.error('Failed to parse token:', error)
      return null
    }
  }

  function initialize() {
    const storedToken = localStorage.getItem('token')
    if (storedToken) {
      token.value = storedToken
      fetchUser()
    }
  }

  return {
    user,
    token,
    isAuthenticated,
    currentUser,
    login,
    logout,
    initialize,
    fetchUser,
    getOAuthLoginUrls,
    getEmailFromToken
  }
}) 