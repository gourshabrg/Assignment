package com.Capstone.InterviewTracking.dto;

/**
 * Response DTO returned upon successful authentication.
 */
public class AuthResponse {

    private final String token;
    private final String email;

    /** The authenticated user's role (e.g., HR, PANEL, CANDIDATE). */
    private final String role;

    /**
     * Creates an AuthResponse with all fields.
     *
     * @param token the signed JWT token
     * @param email the authenticated user's email address
     * @param role the user's role name
     */
    public AuthResponse(final String token, final String email, final String role) {
        this.token = token;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }

    public String getEmail() { return email; }

    public String getRole() { return role; }
}
