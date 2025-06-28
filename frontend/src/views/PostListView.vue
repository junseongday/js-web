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
          <tr v-for="post in postResponse.posts" :key="post.id" @click="goToDetail(post.id)" class="post-row">
            <td>{{ post.title }}</td>
            <td>
              {{ post.authorNickname }}
              <span v-if="authStore.currentUser && authStore.currentUser.id === post.authorId" class="author-badge">(본인)</span>
            </td>
            <td>{{ formatDate(post.createdAt as unknown as number[]) }}</td>
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
import '@/assets/styles/posts.css'
import { formatDate } from '@/utils/date'

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

const goToDetail = (id: number) => {
  router.push(`/posts/${id}`)
}

onMounted(fetchPosts)

watch(() => route.query, fetchPosts, { immediate: true })
</script> 