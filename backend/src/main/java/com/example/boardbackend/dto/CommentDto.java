package com.example.boardbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CommentDto {
    
    public static class CreateRequest {
        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(min = 1, max = 1000, message = "댓글 내용은 1자 이상 1000자 이하여야 합니다.")
        private String content;
        
        // Getters and Setters
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
    
    public static class UpdateRequest {
        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(min = 1, max = 1000, message = "댓글 내용은 1자 이상 1000자 이하여야 합니다.")
        private String content;
        
        // Getters and Setters
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
    
    public static class CommentInfo {
        private Long id;
        private String content;
        private String authorNickname;
        private Long authorId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean isAuthor;
        
        public CommentInfo(Long id, String content, String authorNickname, Long authorId, 
                          LocalDateTime createdAt, LocalDateTime updatedAt, boolean isAuthor) {
            this.id = id;
            this.content = content;
            this.authorNickname = authorNickname;
            this.authorId = authorId;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.isAuthor = isAuthor;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
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
    }
} 