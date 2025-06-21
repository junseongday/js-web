<template>
  <div class="post-list-container">
    <div class="header">
      <h1>게시물 목록</h1>
      <RouterLink v-if="authStore.isAuthenticated" to="/posts/create" class="btn-create">
        새 글 작성
      </RouterLink>
    </div>

    <div v-if="loading" class="loading">로딩 중...</div>
    <div v-if="error" class="error">{{ error }}</div>

    <div v-if="postResponse" class="content">
      <table class="post-table">
        <thead>
          <tr>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="post in postResponse.posts" :key="post.id">
            <td>
              <RouterLink :to="`/posts/${post.id}`" class="post-title">{{ post.title }}</RouterLink>
            </td>
            <td>{{ post.authorNickname }} <span v-if="post.isAuthor" class="author-badge">(본인)</span></td>
            <td>{{ new Date(post.createdAt).toLocaleDateString() }}</td>
          </tr>
        </tbody>
      </table>

      <div class="pagination">
        <button @click="prevPage" :disabled="!postResponse.hasPrevious">이전</button>
        <span>페이지 {{ postResponse.currentPage + 1 }} / {{ postResponse.totalPages }}</span>
        <button @click="nextPage" :disabled="!postResponse.hasNext">다음</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPosts } from '@/api'
import type { PostListResponse } from '@/types'
import { useAuthStore } from '@/stores/auth'

const postResponse = ref<PostListResponse | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)
const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const page = ref(Number(route.query.page) || 0)
const size = ref(Number(route.query.size) || 10)

const fetchPosts = async () => {
  loading.value = true
  try {
    const { data } = await getPosts(page.value, size.value)
    postResponse.value = data
  } catch (err) {
    error.value = '게시글을 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

const updateRoute = () => {
  router.push({ query: { page: page.value, size: size.value } })
}

const prevPage = () => {
  if (postResponse.value?.hasPrevious) {
    page.value--
    updateRoute()
  }
}

const nextPage = () => {
  if (postResponse.value?.hasNext) {
    page.value++
    updateRoute()
  }
}

onMounted(fetchPosts)

watch(() => route.query, fetchPosts, { immediate: true })
</script>

<style scoped>
.post-list-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 1rem;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}
.btn-create {
  background-color: #007bff;
  color: white;
  padding: 10px 18px;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 500;
}
.loading, .error {
  text-align: center;
  padding: 2rem;
  color: #555;
}
.post-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1.5rem;
}
.post-table th, .post-table td {
  border-bottom: 1px solid #e0e0e0;
  padding: 12px 16px;
  text-align: left;
}
.post-table th {
  background-color: #f9f9f9;
  font-weight: bold;
}
.post-title {
  color: #333;
  font-weight: 500;
  text-decoration: none;
}
.post-title:hover {
  text-decoration: underline;
}
.author-badge {
  color: #007bff;
  font-size: 0.8rem;
  font-weight: bold;
}
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
}
</style> 