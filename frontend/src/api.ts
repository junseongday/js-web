import axios from 'axios'
import type {
  LoginRequest,
  LoginResponse,
  SignupRequest,
  User,
  PostListResponse,
  Post,
  CreatePostRequest,
  UpdatePostRequest,
  Comment,
  CreateCommentRequest,
  UpdateCommentRequest,
  OAuthUrls
} from './types'

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Auth API
export const signup = (data: SignupRequest) => apiClient.post<User>('/auth/signup', data)
export const login = (data: LoginRequest) => apiClient.post<LoginResponse>('/auth/login', data)
export const getMe = () => apiClient.get<User>('/auth/me')

// OAuth API
export const getOAuthUrls = () => apiClient.get<OAuthUrls>('/oauth/urls')

// Post API
export const getPosts = (page: number, size: number) =>
  apiClient.get<PostListResponse>(`/posts?page=${page}&size=${size}`)
export const getPost = (id: number) => apiClient.get<Post>(`/posts/${id}`)
export const createPost = (data: CreatePostRequest) => apiClient.post<Post>('/posts', data)
export const updatePost = (id: number, data: UpdatePostRequest) =>
  apiClient.put<Post>(`/posts/${id}`, data)
export const deletePost = (id: number) => apiClient.delete(`/posts/${id}`)

// Comment API
export const getComments = (postId: number) => apiClient.get<Comment[]>(`/comments/post/${postId}`)
export const createComment = (postId: number, data: CreateCommentRequest) =>
  apiClient.post<Comment>(`/comments/post/${postId}`, data)
export const updateComment = (id: number, data: UpdateCommentRequest) =>
  apiClient.put<Comment>(`/comments/${id}`, data)
export const deleteComment = (id: number) => apiClient.delete(`/comments/${id}`) 