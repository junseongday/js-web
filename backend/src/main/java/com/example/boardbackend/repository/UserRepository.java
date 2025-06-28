package com.example.boardbackend.repository;

import com.example.boardbackend.entity.AuthProvider;
import com.example.boardbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderId(String providerId);
    Optional<User> findByEmailAndAuthProvider(String email, AuthProvider authProvider);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
} 