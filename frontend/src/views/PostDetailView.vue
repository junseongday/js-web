<template>
  <div class="post-detail-container">
    <div v-if="loading" class="loading">로딩 중...</div>
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="post" class="post-content">
      <!-- 게시글 카드 -->
      <div class="post-card card">
        <div class="post-header">
          <div class="post-meta">
            <div class="author-info">
              <div class="author-avatar">
                {{ post.authorNickname.charAt(0).toUpperCase() }}
              </div>
              <div class="author-details">
                <div class="author-name">{{ post.authorNickname }}</div>
                <div class="post-date">{{ formatDate(post.createdAt as unknown as number[]) }}</div>
              </div>
            </div>
            <div v-if="post.author" class="post-actions">
              <RouterLink :to="`/posts/${post.id}/edit`" class="btn btn-secondary">수정</RouterLink>
              <button @click="handleDelete" class="btn btn-danger">삭제</button>
            </div>
          </div>
        </div>

        <div class="post-body">
          <h1 class="post-title">{{ post.title }}</h1>
          <div class="post-content-text" v-html="post.content"></div>
        </div>
      </div>
      
      <!-- 댓글 섹션 -->
      <div class="comments-card card">
        <h2 class="comments-title">댓글</h2>
        
        <!-- 댓글 작성 폼 -->
        <div v-if="authStore.isAuthenticated" class="comment-form">
          <textarea 
            v-model="newCommentContent" 
            placeholder="댓글을 입력하세요..."
            class="comment-input"
          ></textarea>
          <button @click="handleCreateComment" class="btn comment-submit-btn">댓글 작성</button>
        </div>
        
        <!-- 댓글 목록 -->
        <div class="comments-list">
          <div v-if="post.comments.length === 0" class="no-comments">
            아직 댓글이 없습니다.
          </div>
          <div v-for="comment in post.comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <div class="comment-author">
                <div class="comment-avatar">
                  {{ comment.authorNickname.charAt(0).toUpperCase() }}
                </div>
                <div class="comment-author-info">
                  <span class="comment-author-name">{{ comment.authorNickname }}</span>
                  <span class="comment-date">{{ formatDate(comment.createdAt as unknown as number[]) }}</span>
                </div>
              </div>
              <div v-if="comment.author && editingComment?.id !== comment.id" class="comment-actions">
                <button @click="startEditComment(comment)" class="btn-text">수정</button>
                <button @click="handleDeleteComment(comment.id)" class="btn-text btn-text-danger">삭제</button>
              </div>
            </div>
            
            <div v-if="editingComment?.id === comment.id" class="comment-edit-form">
              <textarea v-model="editingComment.content" class="comment-input"></textarea>
              <div class="comment-edit-actions">
                <button @click="handleUpdateComment(comment.id, editingComment.content)" class="btn">저장</button>
                <button @click="cancelEditComment" class="btn btn-secondary">취소</button>
              </div>
            </div>
            <div v-else class="comment-content">
              {{ comment.content }}
            </div>
          </div>
        </div>
      </div>
      
      <!-- 하단 액션 -->
      <div class="bottom-actions">
        <button @click="goToList" class="btn btn-back">← 목록으로</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { getPost, deletePost, createComment, deleteComment, updateComment } from '@/api'
import type { Post, Comment } from '@/types'
import { useAuthStore } from '@/stores/auth'
import '@/assets/styles/posts.css'
import { formatDate } from '@/utils/date'

const props = defineProps({
  id: {
    type: String,
    required: true
  }
})

const post = ref<Post | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)
const router = useRouter()
const authStore = useAuthStore()
const newCommentContent = ref('')
const editingComment = ref<Comment | null>(null)

const fetchPost = async () => {
  loading.value = true
  try {
    const { data } = await getPost(Number(props.id))
    console.log('Post data:', data)
    console.log('author:', data.author)
    console.log('Comments:', data.comments)
    console.log('Auth store isAuthenticated:', authStore.isAuthenticated)
    console.log('Auth store currentUser:', authStore.currentUser)
    post.value = data
  } catch (err) {
    console.error('Error fetching post:', err)
    error.value = '게시글을 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

const handleDelete = async () => {
  if (confirm('정말로 삭제하시겠습니까?')) {
    try {
      await deletePost(Number(props.id))
      router.push('/posts')
    } catch (err) {
      alert('삭제에 실패했습니다.')
    }
  }
}

const handleCreateComment = async () => {
  if (!newCommentContent.value.trim()) return
  try {
    await createComment(Number(props.id), { content: newCommentContent.value })
    newCommentContent.value = ''
    await fetchPost()
  } catch (err) {
    alert('댓글 작성에 실패했습니다.')
  }
}

const handleDeleteComment = async (commentId: number) => {
  if (confirm('정말로 댓글을 삭제하시겠습니까?')) {
    try {
      await deleteComment(commentId)
      await fetchPost()
    } catch (err) {
      alert('댓글 삭제에 실패했습니다.')
    }
  }
}

const startEditComment = (comment: Comment) => {
  editingComment.value = { ...comment }
}

const cancelEditComment = () => {
  editingComment.value = null
}

const handleUpdateComment = async (commentId: number, content: string) => {
  try {
    await updateComment(commentId, { content })
    editingComment.value = null
    await fetchPost()
  } catch (err) {
    alert('댓글 수정에 실패했습니다.')
  }
}

const goToList = () => {
  router.push('/posts')
}

onMounted(fetchPost)
</script> 