<template>
  <Transition name="toast">
    <div v-if="show" :class="['toast', `toast-${type}`]">
      <div class="toast-content">
        <span class="toast-icon">{{ icon }}</span>
        <span class="toast-message">{{ message }}</span>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import '@/assets/styles/components.css'

interface Props {
  show: boolean
  message: string
  type?: 'success' | 'error' | 'info' | 'warning'
}

const props = withDefaults(defineProps<Props>(), {
  type: 'info'
})

const icon = computed(() => {
  switch (props.type) {
    case 'success': return '✓'
    case 'error': return '✕'
    case 'warning': return '⚠'
    case 'info': return 'ℹ'
    default: return 'ℹ'
  }
})
</script> 