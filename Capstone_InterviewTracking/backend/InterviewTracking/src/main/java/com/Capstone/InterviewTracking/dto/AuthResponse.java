package com.Capstone.InterviewTracking.dto;

/**
 * Response DTO returned upon successful authentication.
 */
public class AuthResponse {

    private String token;
    private String email;

    /** The authenticated user's role (e.g., HR, PANEL, CANDIDATE). */
    private String role;

    /**
     * Creates an AuthResponse with all fields.
     *
     * @param token the signed JWT token
     * @param email the authenticated user's email address
     * @param role the user's role name
     */
    public AuthResponse(String token, String email, String role) {
        this.token = token;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
