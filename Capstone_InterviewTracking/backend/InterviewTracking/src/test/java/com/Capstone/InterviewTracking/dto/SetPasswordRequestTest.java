package com.Capstone.InterviewTracking.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetPasswordRequestTest {

    @Test
    void settersAndGetters() {
        SetPasswordRequest req = new SetPasswordRequest();
        req.setToken("my-token-123");
        req.setPassword("securepass");

        assertEquals("my-token-123", req.getToken());
        assertEquals("securepass", req.getPassword());
    }
}
