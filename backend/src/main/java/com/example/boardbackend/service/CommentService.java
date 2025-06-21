package com.example.boardbackend.service;

import com.example.boardbackend.dto.CommentDto;
import com.example.boardbackend.entity.Comment;
import com.example.boardbackend.entity.Post;
import com.example.boardbackend.entity.User;
import com.example.boardbackend.repository.CommentRepository;
import com.example.boardbackend.repository.PostRepository;
import com.example.boardbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<CommentDto.CommentInfo> getCommentsByPostId(Long postId, String currentUserEmail) {
        List<Comment> comments = commentRepository.findByPostIdWithUserOrderByCreatedAtAsc(postId);
        
        return comments.stream()
                .map(comment -> {
                    boolean isAuthor = currentUserEmail != null && 
                            comment.getUser().getEmail().equals(currentUserEmail);
                    return new CommentDto.CommentInfo(
                            comment.getId(),
                            comment.getContent(),
                            comment.getUser().getNickname(),
                            comment.getUser().getId(),
                            comment.getCreatedAt(),
                            comment.getUpdatedAt(),
                            isAuthor
                    );
                })
                .collect(Collectors.toList());
    }
    
    public CommentDto.CommentInfo createComment(Long postId, CommentDto.CreateRequest createRequest, String currentUserEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Comment comment = new Comment();
        comment.setContent(createRequest.getContent());
        comment.setPost(post);
        comment.setUser(user);
        
        Comment savedComment = commentRepository.save(comment);
        
        return new CommentDto.CommentInfo(
                savedComment.getId(),
                savedComment.getContent(),
                savedComment.getUser().getNickname(),
                savedComment.getUser().getId(),
                savedComment.getCreatedAt(),
                savedComment.getUpdatedAt(),
                true
        );
    }
    
    public CommentDto.CommentInfo updateComment(Long commentId, CommentDto.UpdateRequest updateRequest, String currentUserEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        
        if (!comment.getUser().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("댓글을 수정할 권한이 없습니다.");
        }
        
        comment.setContent(updateRequest.getContent());
        
        Comment updatedComment = commentRepository.save(comment);
        
        return new CommentDto.CommentInfo(
                updatedComment.getId(),
                updatedComment.getContent(),
                updatedComment.getUser().getNickname(),
                updatedComment.getUser().getId(),
                updatedComment.getCreatedAt(),
                updatedComment.getUpdatedAt(),
                true
        );
    }
    
    public void deleteComment(Long commentId, String currentUserEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        
        if (!comment.getUser().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("댓글을 삭제할 권한이 없습니다.");
        }
        
        commentRepository.delete(comment);
    }
} 