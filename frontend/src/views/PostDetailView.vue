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
                <div class="post-date">{{ formatDate(post.createdAt) }}</div>
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
                  <div class="comment-author-name">{{ comment.authorNickname }}</div>
                  <div class="comment-date">{{ formatDate(comment.createdAt) }}</div>
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

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffInHours = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60))
  
  if (diffInHours < 1) {
    return '방금 전'
  } else if (diffInHours < 24) {
    return `${diffInHours}시간 전`
  } else if (diffInHours < 168) { // 7일
    const days = Math.floor(diffInHours / 24)
    return `${days}일 전`
  } else {
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  }
}

onMounted(fetchPost)
</script>

<style scoped>
.post-detail-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.post-card {
  background: var(--card-background);
  border-radius: 12px;
  box-shadow: var(--shadow);
  margin-bottom: 24px;
  overflow: hidden;
}

.post-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}

.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(45deg, var(--primary-color), var(--secondary-color));
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 18px;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.author-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
}

.post-date {
  color: var(--text-secondary);
  font-size: 12px;
}

.post-actions {
  display: flex;
  gap: 8px;
}

.post-body {
  padding: 20px;
}

.post-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 16px;
  line-height: 1.3;
}

.post-content-text {
  color: var(--text-primary);
  line-height: 1.6;
  font-size: 16px;
}

.comments-card {
  background: var(--card-background);
  border-radius: 12px;
  box-shadow: var(--shadow);
  padding: 20px;
  margin-bottom: 24px;
}

.comments-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
}

.comment-form {
  margin-bottom: 24px;
}

.comment-input {
  width: 100%;
  min-height: 80px;
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  margin-bottom: 12px;
  background: var(--card-background);
  transition: border-color 0.2s ease;
}

.comment-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(0, 149, 246, 0.1);
}

.comment-submit-btn {
  background: var(--primary-color);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.comment-submit-btn:hover {
  background: var(--secondary-color);
  transform: translateY(-1px);
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.no-comments {
  text-align: center;
  color: var(--text-secondary);
  font-size: 14px;
  padding: 32px 0;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--border-color);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.comment-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.comment-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(45deg, var(--primary-color), var(--secondary-color));
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 14px;
}

.comment-author-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.comment-author-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 13px;
}

.comment-date {
  color: var(--text-secondary);
  font-size: 11px;
}

.comment-actions {
  display: flex;
  gap: 8px;
}

.btn-text {
  background: none;
  border: none;
  padding: 4px 8px;
  cursor: pointer;
  color: var(--text-secondary);
  font-size: 12px;
  font-weight: 500;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.btn-text:hover {
  background: var(--background-color);
  color: var(--text-primary);
}

.btn-text-danger {
  color: var(--error-color);
}

.btn-text-danger:hover {
  background: rgba(237, 73, 86, 0.1);
}

.comment-edit-form {
  margin-top: 12px;
}

.comment-edit-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.comment-content {
  color: var(--text-primary);
  font-size: 14px;
  line-height: 1.5;
  margin-left: 44px;
}

.bottom-actions {
  text-align: center;
  margin-top: 32px;
}

.btn-back {
  background: var(--background-color);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.2s ease;
}

.btn-back:hover {
  background: var(--card-background);
  border-color: var(--text-secondary);
  transform: translateY(-1px);
  box-shadow: var(--shadow);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .post-detail-container {
    padding: 16px;
  }
  
  .post-card,
  .comments-card {
    border-radius: 8px;
  }
  
  .post-header,
  .post-body {
    padding: 16px;
  }
  
  .post-title {
    font-size: 20px;
  }
  
  .author-avatar {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
  
  .comment-avatar {
    width: 28px;
    height: 28px;
    font-size: 12px;
  }
}
</style> 