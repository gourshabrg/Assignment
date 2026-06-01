package com.Capstone.InterviewTracking.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppConstantsTest {

    @Test
    void buildEmailMessage_containsNameAndVerificationLink() {
        String result = AppConstants.buildEmailMessage("John", "abc-token-123");

        assertTrue(result.contains("John"));
        assertTrue(result.contains("abc-token-123"));
        assertTrue(result.contains("Verify Account"));
        assertTrue(result.contains("<html>"));
    }

    @Test
    void buildInterviewScheduledMessage_containsRoundAndDateTime() {
        String result = AppConstants.buildInterviewScheduledMessage(
                "Jane", "L1", "2025-06-01T10:00", "Alice, Bob");

        assertTrue(result.contains("Jane"));
        assertTrue(result.contains("L1"));
        assertTrue(result.contains("2025-06-01T10:00"));
        assertTrue(result.contains("Alice, Bob"));
    }

    @Test
    void buildHRInterviewScheduledMessage_containsCandidateInfo() {
        String result = AppConstants.buildHRInterviewScheduledMessage(
                "hr@example.com", "Jane Doe", "jane@example.com", "2025-06-02T14:00");

        assertTrue(result.contains("Jane Doe"));
        assertTrue(result.contains("jane@example.com"));
        assertTrue(result.contains("2025-06-02T14:00"));
        assertTrue(result.contains("HR Round"));
    }

    @Test
    void constants_authPaths_areCorrectlyDefined() {
        assertEquals("/auth", AppConstants.AUTH_BASE_PATH);
        assertEquals("/auth/signup", AppConstants.AUTH_BASE_PATH + AppConstants.REGISTER_PATH);
        assertEquals("/auth/login", AppConstants.AUTH_BASE_PATH + AppConstants.LOGIN_PATH);
    }

    @Test
    void constants_setTokenExpiry_isFifteenMinutes() {
        assertEquals(15, AppConstants.SET_TOKEN_EXPIRY);
    }
}
