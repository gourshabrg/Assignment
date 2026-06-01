package com.Capstone.InterviewTracking.mapper;

import com.Capstone.InterviewTracking.dto.AuthRequest;
import com.Capstone.InterviewTracking.entity.User;
import com.Capstone.InterviewTracking.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper mapper;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        mapper = new UserMapper();
        passwordEncoder = new BCryptPasswordEncoder();
    }

    // ── toUser ────────────────────────────────────────────────────────────────────

    @Test
    void toUser_withRole_mapsAllFields() {
        AuthRequest req = new AuthRequest();
        req.setEmail("hr@example.com");
        req.setPassword("plainPassword");
        req.setRole(RoleType.HR);

        User result = mapper.toUser(req, "hr@example.com", passwordEncoder);

        assertEquals("hr@example.com", result.getEmail());
        assertEquals(RoleType.HR, result.getRole());
        assertTrue(passwordEncoder.matches("plainPassword", result.getPassword()));
    }

    @Test
    void toUser_withNullRole_defaultsToCandidate() {
        AuthRequest req = new AuthRequest();
        req.setEmail("user@example.com");
        req.setPassword("pass123");
        req.setRole(null);

        User result = mapper.toUser(req, "user@example.com", passwordEncoder);

        assertEquals(RoleType.CANDIDATE, result.getRole());
    }

    // ── toUserForSignup ───────────────────────────────────────────────────────────

    @Test
    void toUserForSignup_withRole_mapsEmailAndRole() {
        User result = mapper.toUserForSignup("panel@example.com", RoleType.PANEL);

        assertEquals("panel@example.com", result.getEmail());
        assertEquals(RoleType.PANEL, result.getRole());
        assertFalse(result.isVerified());
    }

    @Test
    void toUserForSignup_withNullRole_defaultsToCandidate() {
        User result = mapper.toUserForSignup("user@example.com", null);

        assertEquals(RoleType.CANDIDATE, result.getRole());
        assertFalse(result.isVerified());
    }
}
