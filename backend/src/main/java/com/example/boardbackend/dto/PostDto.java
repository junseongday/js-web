package com.example.boardbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class PostDto {
    
    public static class CreateRequest {
        @NotBlank(message = "제목은 필수입니다.")
        @Size(min = 1, max = 200, message = "제목은 1자 이상 200자 이하여야 합니다.")
        private String title;
        
        @NotBlank(message = "내용은 필수입니다.")
        @Size(min = 1, max = 10000, message = "내용은 1자 이상 10000자 이하여야 합니다.")
        private String content;
        
        // Getters and Setters
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
    
    public static class UpdateRequest {
        @NotBlank(message = "제목은 필수입니다.")
        @Size(min = 1, max = 200, message = "제목은 1자 이상 200자 이하여야 합니다.")
        private String title;
        
        @NotBlank(message = "내용은 필수입니다.")
        @Size(min = 1, max = 10000, message = "내용은 1자 이상 10000자 이하여야 합니다.")
        private String content;
        
        // Getters and Setters
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
    
    public static class PostSummary {
        private Long id;
        private String title;
        private String authorNickname;
        private Long authorId;
        private LocalDateTime createdAt;
        private boolean isAuthor;
        
        public PostSummary(Long id, String title, String authorNickname, Long authorId, LocalDateTime createdAt, boolean isAuthor) {
            this.id = id;
            this.title = title;
            this.authorNickname = authorNickname;
            this.authorId = authorId;
            this.createdAt = createdAt;
            this.isAuthor = isAuthor;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getAuthorNickname() {
            return authorNickname;
        }
        
        public void setAuthorNickname(String authorNickname) {
            this.authorNickname = authorNickname;
        }
        
        public Long getAuthorId() {
            return authorId;
        }
        
        public void setAuthorId(Long authorId) {
            this.authorId = authorId;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
        
        public boolean isAuthor() {
            return isAuthor;
        }
        
        public void setAuthor(boolean author) {
            isAuthor = author;
        }
    }
    
    public static class PostDetail {
        private Long id;
        private String title;
        private String content;
        private String authorNickname;
        private Long authorId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean isAuthor;
        private List<CommentDto.CommentInfo> comments;
        
        public PostDetail(Long id, String title, String content, String authorNickname, Long authorId, 
                         LocalDateTime createdAt, LocalDateTime updatedAt, boolean isAuthor, List<CommentDto.CommentInfo> comments) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.authorNickname = authorNickname;
            this.authorId = authorId;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.isAuthor = isAuthor;
            this.comments = comments;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public String getAuthorNickname() {
            return authorNickname;
        }
        
        public void setAuthorNickname(String authorNickname) {
            this.authorNickname = authorNickname;
        }
        
        public Long getAuthorId() {
            return authorId;
        }
        
        public void setAuthorId(Long authorId) {
            this.authorId = authorId;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
        
        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
        
        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
        
        public boolean isAuthor() {
            return isAuthor;
        }
        
        public void setAuthor(boolean author) {
            isAuthor = author;
        }
        
        public List<CommentDto.CommentInfo> getComments() {
            return comments;
        }
        
        public void setComments(List<CommentDto.CommentInfo> comments) {
            this.comments = comments;
        }
    }
    
    public static class PostListResponse {
        private List<PostSummary> posts;
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private boolean hasNext;
        private boolean hasPrevious;
        
        public PostListResponse(List<PostSummary> posts, int currentPage, int totalPages, long totalElements, boolean hasNext, boolean hasPrevious) {
            this.posts = posts;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
        }
        
        // Getters and Setters
        public List<PostSummary> getPosts() {
            return posts;
        }
        
        public void setPosts(List<PostSummary> posts) {
            this.posts = posts;
        }
        
        public int getCurrentPage() {
            return currentPage;
        }
        
        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }
        
        public int getTotalPages() {
            return totalPages;
        }
        
        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
        
        public long getTotalElements() {
            return totalElements;
        }
        
        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }
        
        public boolean isHasNext() {
            return hasNext;
        }
        
        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }
        
        public boolean isHasPrevious() {
            return hasPrevious;
        }
        
        public void setHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }
    }
} 