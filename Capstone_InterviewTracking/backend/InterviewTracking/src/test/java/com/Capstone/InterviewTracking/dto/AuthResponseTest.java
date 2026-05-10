package com.Capstone.InterviewTracking.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void getters() {
        AuthResponse resp = new AuthResponse("tok", "user@example.com", "HR");

        assertEquals("tok", resp.getToken());
        assertEquals("user@example.com", resp.getEmail());
        assertEquals("HR", resp.getRole());
    }
}
