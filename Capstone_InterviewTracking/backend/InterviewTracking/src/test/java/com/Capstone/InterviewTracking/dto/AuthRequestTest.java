package com.Capstone.InterviewTracking.dto;

import com.Capstone.InterviewTracking.enums.RoleType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthRequestTest {

    @Test
    void settersAndGetters() {
        AuthRequest req = new AuthRequest();
        req.setEmail("test@example.com");
        req.setPassword("pass123");
        req.setRole(RoleType.HR);

        assertEquals("test@example.com", req.getEmail());
        assertEquals("pass123", req.getPassword());
        assertEquals(RoleType.HR, req.getRole());
    }
}
