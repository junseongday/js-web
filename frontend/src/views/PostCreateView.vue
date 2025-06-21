<template>
  <div class="form-container">
    <h1>새 글 작성</h1>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="title">제목</label>
        <input type="text" id="title" v-model="title" required />
      </div>
      <div class="form-group">
        <label for="content">내용</label>
        <textarea id="content" v-model="content" required rows="10"></textarea>
      </div>
      <button type="submit" class="btn-submit">작성</button>
      <p v-if="error" class="error-message">{{ error }}</p>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { createPost } from '@/api'

const title = ref('')
const content = ref('')
const error = ref<string | null>(null)
const router = useRouter()

const handleSubmit = async () => {
  try {
    const { data } = await createPost({ title: title.value, content: content.value })
    router.push(`/posts/${data.id}`)
  } catch (err) {
    error.value = '게시글 작성에 실패했습니다.'
  }
}
</script>

<style scoped>
.form-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
}
h1 {
  text-align: center;
  margin-bottom: 1.5rem;
}
.form-group {
  margin-bottom: 1.5rem;
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
</style> 