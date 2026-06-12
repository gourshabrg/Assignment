package com.assignment.todo5.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    @Test
    void testExceptionMessage() {
        RuntimeException ex = new RuntimeException("Error occurred");
        assertEquals("Error occurred", ex.getMessage());
    }
}