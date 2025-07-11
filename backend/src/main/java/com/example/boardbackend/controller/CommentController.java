package com.example.boardbackend.controller;

import com.example.boardbackend.dto.CommentDto;
import com.example.boardbackend.service.CommentService;
import com.example.boardbackend.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "댓글 API")
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @GetMapping("/post/{postId}")
    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 댓글 목록을 조회합니다.")
    public ResponseEntity<List<CommentDto.CommentInfo>> getCommentsByPostId(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        String currentUserEmail = tokenProvider.getEmailFromJWT(token);
        String currentUserProvider = tokenProvider.getProviderFromJWT(token);
        
        List<CommentDto.CommentInfo> comments = commentService.getCommentsByPostId(postId, currentUserEmail, currentUserProvider);
        return ResponseEntity.ok(comments);
    }
    
    @PostMapping("/post/{postId}")
    @Operation(summary = "댓글 작성", description = "특정 게시글에 새로운 댓글을 작성합니다.")
    public ResponseEntity<CommentDto.CommentInfo> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentDto.CreateRequest createRequest,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        String currentUserEmail = tokenProvider.getEmailFromJWT(token);
        String currentUserProvider = tokenProvider.getProviderFromJWT(token);
        
        System.out.println("=== Comment Create Debug ===");
        System.out.println("Current user email: " + currentUserEmail);
        System.out.println("Current user provider: " + currentUserProvider);
        
        if (currentUserEmail == null || currentUserProvider == null) {
            return ResponseEntity.status(401).build();
        }
        
        CommentDto.CommentInfo commentInfo = commentService.createComment(postId, createRequest, currentUserEmail, currentUserProvider);
        return ResponseEntity.ok(commentInfo);
    }
    
    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "기존 댓글을 수정합니다.")
    public ResponseEntity<CommentDto.CommentInfo> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDto.UpdateRequest updateRequest,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        String currentUserEmail = tokenProvider.getEmailFromJWT(token);
        String currentUserProvider = tokenProvider.getProviderFromJWT(token);
        
        if (currentUserEmail == null || currentUserProvider == null) {
            return ResponseEntity.status(401).build();
        }
        
        CommentDto.CommentInfo commentInfo = commentService.updateComment(commentId, updateRequest, currentUserEmail, currentUserProvider);
        return ResponseEntity.ok(commentInfo);
    }
    
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        String currentUserEmail = tokenProvider.getEmailFromJWT(token);
        String currentUserProvider = tokenProvider.getProviderFromJWT(token);
        
        if (currentUserEmail == null || currentUserProvider == null) {
            return ResponseEntity.status(401).build();
        }
        
        commentService.deleteComment(commentId, currentUserEmail, currentUserProvider);
        return ResponseEntity.ok().build();
    }
} 