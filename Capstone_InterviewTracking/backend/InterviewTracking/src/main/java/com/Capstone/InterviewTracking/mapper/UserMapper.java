package com.Capstone.InterviewTracking.mapper;

import com.Capstone.InterviewTracking.dto.AuthRequest;
import com.Capstone.InterviewTracking.entity.User;
import com.Capstone.InterviewTracking.enums.RoleType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Mapper component for creating User entities from authentication DTOs.
 */
@Component
public class UserMapper {

    /**
     * Creates a User entity from a login request.
     *
     * @param request the authentication request
     * @param normalizedEmail the normalized email address
     * @param passwordEncoder the password encoder
     * @return the user entity
     */
    public User toUser(AuthRequest request, String normalizedEmail, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() == null ? RoleType.CANDIDATE : request.getRole());
        return user;
    }

    /**
     * Creates a User entity for the signup flow without a password.
     *
     * @param normalizedEmail the normalized email address
     * @param role the role to assign
     * @return the user entity
     */
    public User toUserForSignup(String normalizedEmail, RoleType role) {
        User user = new User();
        user.setEmail(normalizedEmail);
        user.setRole(role == null ? RoleType.CANDIDATE : role);
        user.setVerified(false);
        return user;
    }
}
