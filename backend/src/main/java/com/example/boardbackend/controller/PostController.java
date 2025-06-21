package com.example.boardbackend.controller;

import com.example.boardbackend.dto.PostDto;
import com.example.boardbackend.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "게시글 API")
@CrossOrigin(origins = "http://localhost:5173")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "페이징된 게시글 목록을 조회합니다.")
    public ResponseEntity<PostDto.PostListResponse> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        String currentUserEmail = getCurrentUserEmail();
        PostDto.PostListResponse response = postService.getPosts(page, size, currentUserEmail);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    public ResponseEntity<PostDto.PostDetail> getPost(@PathVariable Long postId) {
        String currentUserEmail = getCurrentUserEmail();
        System.out.println("Current user email: " + currentUserEmail);
        PostDto.PostDetail postDetail = postService.getPost(postId, currentUserEmail);
        return ResponseEntity.ok(postDetail);
    }
    
    @PostMapping
    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    public ResponseEntity<PostDto.PostSummary> createPost(@Valid @RequestBody PostDto.CreateRequest createRequest) {
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail == null) {
            return ResponseEntity.status(401).build();
        }
        
        PostDto.PostSummary postSummary = postService.createPost(createRequest, currentUserEmail);
        return ResponseEntity.ok(postSummary);
    }
    
    @PutMapping("/{postId}")
    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    public ResponseEntity<PostDto.PostSummary> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostDto.UpdateRequest updateRequest) {
        
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail == null) {
            return ResponseEntity.status(401).build();
        }
        
        PostDto.PostSummary postSummary = postService.updatePost(postId, updateRequest, currentUserEmail);
        return ResponseEntity.ok(postSummary);
    }
    
    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail == null) {
            return ResponseEntity.status(401).build();
        }
        
        postService.deletePost(postId, currentUserEmail);
        return ResponseEntity.ok().build();
    }
    
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }
} 