package com.example.boardbackend.service;

import com.example.boardbackend.dto.PostDto;
import com.example.boardbackend.dto.CommentDto;
import com.example.boardbackend.entity.Post;
import com.example.boardbackend.entity.User;
import com.example.boardbackend.repository.PostRepository;
import com.example.boardbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CommentService commentService;
    
    public PostDto.PostListResponse getPosts(int page, int size, String currentUserEmail) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        List<PostDto.PostSummary> postSummaries = postPage.getContent().stream()
                .map(post -> {
                    boolean isAuthor = currentUserEmail != null && 
                            post.getUser().getEmail().equals(currentUserEmail);
                    return new PostDto.PostSummary(
                            post.getId(),
                            post.getTitle(),
                            post.getUser().getNickname(),
                            post.getUser().getId(),
                            post.getCreatedAt(),
                            isAuthor
                    );
                })
                .collect(Collectors.toList());
        
        return new PostDto.PostListResponse(
                postSummaries,
                postPage.getNumber(),
                postPage.getTotalPages(),
                postPage.getTotalElements(),
                postPage.hasNext(),
                postPage.hasPrevious()
        );
    }
    
    public PostDto.PostDetail getPost(Long postId, String currentUserEmail) {
        Post post = postRepository.findByIdWithUser(postId);
        if (post == null) {
            throw new RuntimeException("게시글을 찾을 수 없습니다.");
        }
        
        boolean isAuthor = currentUserEmail != null && 
                post.getUser().getEmail().equals(currentUserEmail);
        
        List<CommentDto.CommentInfo> comments = commentService.getCommentsByPostId(postId, currentUserEmail);
        
        return new PostDto.PostDetail(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getNickname(),
                post.getUser().getId(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                isAuthor,
                comments
        );
    }
    
    public PostDto.PostSummary createPost(PostDto.CreateRequest createRequest, String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Post post = new Post();
        post.setTitle(createRequest.getTitle());
        post.setContent(createRequest.getContent());
        post.setUser(user);
        
        Post savedPost = postRepository.save(post);
        
        return new PostDto.PostSummary(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getUser().getNickname(),
                savedPost.getUser().getId(),
                savedPost.getCreatedAt(),
                true
        );
    }
    
    public PostDto.PostSummary updatePost(Long postId, PostDto.UpdateRequest updateRequest, String currentUserEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        if (!post.getUser().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("게시글을 수정할 권한이 없습니다.");
        }
        
        post.setTitle(updateRequest.getTitle());
        post.setContent(updateRequest.getContent());
        
        Post updatedPost = postRepository.save(post);
        
        return new PostDto.PostSummary(
                updatedPost.getId(),
                updatedPost.getTitle(),
                updatedPost.getUser().getNickname(),
                updatedPost.getUser().getId(),
                updatedPost.getCreatedAt(),
                true
        );
    }
    
    public void deletePost(Long postId, String currentUserEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        if (!post.getUser().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("게시글을 삭제할 권한이 없습니다.");
        }
        
        postRepository.delete(post);
    }
} 