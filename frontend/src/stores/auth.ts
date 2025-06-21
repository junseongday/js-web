import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getMe } from '@/api'
import type { LoginRequest, User } from '@/types'

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

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('email')
  }

  async function fetchUser() {
    const storedEmail = localStorage.getItem('email')
    if (!token.value || !storedEmail) return
    try {
      const { data } = await getMe(storedEmail)
      user.value = data
    } catch (error) {
      console.error('Failed to fetch user:', error)
      logout()
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
    fetchUser
  }
}) 