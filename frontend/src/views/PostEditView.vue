<template>
  <div class="form-container">
    <h1>게시글 수정</h1>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="title">제목</label>
        <input type="text" id="title" v-model="title" required />
      </div>
      <div class="form-group">
        <label for="content">내용</label>
        <textarea id="content" v-model="content" required rows="10"></textarea>
      </div>
      <button type="submit" class="btn-submit">수정</button>
      <p v-if="error" class="error-message">{{ error }}</p>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPost, updatePost } from '@/api'
import type { Post } from '@/types'
import '@/assets/styles/posts.css'

const props = defineProps({
  id: {
    type: String,
    required: true
  }
})

const title = ref('')
const content = ref('')
const error = ref<string | null>(null)
const router = useRouter()

onMounted(async () => {
  try {
    const { data } = await getPost(Number(props.id))
    title.value = data.title
    content.value = data.content
  } catch (err) {
    error.value = '게시글 정보를 불러오는데 실패했습니다.'
  }
})

const handleSubmit = async () => {
  try {
    await updatePost(Number(props.id), { title: title.value, content: content.value })
    router.push(`/posts/${props.id}`)
  } catch (err) {
    error.value = '게시글 수정에 실패했습니다.'
  }
}
</script> 