<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { RouterLink } from 'vue-router'
import TheWelcome from '@/components/TheWelcome.vue'
import '@/assets/styles/components.css'

const authStore = useAuthStore()
</script>

<template>
  <main>
    <TheWelcome />
  </main>
</template>
