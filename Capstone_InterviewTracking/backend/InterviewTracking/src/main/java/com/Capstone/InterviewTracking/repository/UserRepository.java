package com.Capstone.InterviewTracking.repository;

import com.Capstone.InterviewTracking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email address
     * @return the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their verification token.
     *
     * @param token the verification token
     * @return the user if found
     */
    Optional<User> findByVerificationToken(String token);

    /**
     * Checks if a user with the given email already exists.
     *
     * @param email the email address
     * @return true if a user with that email exists
     */
    boolean existsByEmail(String email);
}
