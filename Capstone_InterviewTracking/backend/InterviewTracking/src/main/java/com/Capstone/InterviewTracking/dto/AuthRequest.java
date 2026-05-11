package com.Capstone.InterviewTracking.dto;

import com.Capstone.InterviewTracking.constant.AppConstants;
import com.Capstone.InterviewTracking.enums.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for user authentication (login).
 */
public class AuthRequest {

    @NotBlank(message = AppConstants.VALIDATION_EMAIL_REQUIRED)
    @Email(message = AppConstants.VALIDATION_EMAIL_INVALID)
    private String email;

    @NotBlank(message = AppConstants.VALIDATION_PASSWORD_REQUIRED)
    @Size(min = 6, message = AppConstants.VALIDATION_PASSWORD_MIN)
    private String password;

    /** Optional role override; defaults to CANDIDATE if not provided. */
    private RoleType role;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }
}
