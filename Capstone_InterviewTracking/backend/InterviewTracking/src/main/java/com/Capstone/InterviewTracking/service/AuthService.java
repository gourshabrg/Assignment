package com.Capstone.InterviewTracking.service;

import com.Capstone.InterviewTracking.dto.AuthRequest;
import com.Capstone.InterviewTracking.dto.AuthResponse;
import com.Capstone.InterviewTracking.dto.SignupRequest;

/**
 * Service interface for user registration, password setup, and login.
 */
public interface AuthService {

    /**
     * Registers a new candidate and sends an email verification link.
     *
     * @param request the signup details
     */
    void register(SignupRequest request);

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request the login credentials
     * @return the authentication response with token and role
     */
    AuthResponse login(AuthRequest request);

    /**
     * Sets the user's password using the one-time verification token.
     *
     * @param token the token from the email link
     * @param password the new password
     */
    void setPassword(String token, String password);
}
