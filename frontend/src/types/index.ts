// 사용자 관련 타입
export interface User {
  id: number
  email: string
  nickname: string
  authProvider?: string
  providerId?: string
  profileImage?: string
}

export interface SignupRequest {
  email: string
  nickname: string
  password: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  token: string
  email: string
  nickname: string
  userId: number
}

// OAuth 관련 타입
export interface OAuthUrls {
  google: string
  kakao: string
  naver: string
}

export interface OAuthRedirectParams {
  token: string
  provider: string
}

// 게시글 관련 타입
export interface Post {
  id: number
  title: string
  content: string
  authorNickname: string
  authorId: number
  createdAt: string
  updatedAt: string
  author: boolean
  comments: Comment[]
}

export interface PostSummary {
  id: number
  title: string
  authorNickname: string
  authorId: number
  createdAt: string
  author: boolean
}

export interface PostListResponse {
  posts: PostSummary[]
  currentPage: number
  totalPages: number
  totalElements: number
  hasNext: boolean
  hasPrevious: boolean
}

export interface CreatePostRequest {
  title: string
  content: string
}

export interface UpdatePostRequest {
  title: string
  content: string
}

// 댓글 관련 타입
export interface Comment {
  id: number
  content: string
  authorNickname: string
  authorId: number
  createdAt: string
  updatedAt: string
  author: boolean
}

export interface CreateCommentRequest {
  content: string
}

export interface UpdateCommentRequest {
  content: string
}

// API 응답 타입
export interface ApiResponse<T> {
  data: T
  message?: string
  error?: string
} 