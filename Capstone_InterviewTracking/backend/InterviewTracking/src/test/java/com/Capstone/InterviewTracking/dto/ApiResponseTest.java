package com.Capstone.InterviewTracking.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void allFactoriesAndSetters() {
        ApiResponse<String> success = ApiResponse.success("OK", "data");
        assertTrue(success.isSuccess());
        assertEquals("OK", success.getMessage());
        assertEquals("data", success.getData());
        assertTrue(success.getErrors().isEmpty());

        ApiResponse<String> failure = ApiResponse.failure("Error");
        assertFalse(failure.isSuccess());
        assertEquals("Error", failure.getMessage());
        assertNull(failure.getData());

        ApiResponse<String> failureWithErrors = ApiResponse.failure("Error", List.of("e1", "e2"));
        assertEquals(2, failureWithErrors.getErrors().size());

        success.setSuccess(false);
        success.setMessage("Updated");
        success.setData("new");
        success.setErrors(List.of("err"));
        assertFalse(success.isSuccess());
        assertEquals("Updated", success.getMessage());
        assertEquals("new", success.getData());
        assertEquals(1, success.getErrors().size());
    }
}
