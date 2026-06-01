package com.Capstone.InterviewTracking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SignupRequestTest {

    @Test
    void settersAndGetters() {
        SignupRequest req = new SignupRequest();
        req.setFullName("John Doe");
        req.setEmail("john@example.com");
        req.setPhone("9876543210");
        req.setDob(LocalDate.of(1995, 5, 15));
        req.setGender("Male");

        assertEquals("John Doe", req.getFullName());
        assertEquals("john@example.com", req.getEmail());
        assertEquals("9876543210", req.getPhone());
        assertEquals(LocalDate.of(1995, 5, 15), req.getDob());
        assertEquals("Male", req.getGender());
    }
}
