package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository; // <-- Change to JpaRepository
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// It's better to extend JpaRepository for more features
public interface UserRepository extends JpaRepository<User, Long> {

    // This is the new method we need for authentication
    Optional<User> findByUsername(String username);

    // This is still useful for registration to check if an email is already taken
    Optional<User> findByEmail(String email);

    // This is still needed for your account verification flow
    Optional<User> findByVerificationCode(String verificationCode);
}