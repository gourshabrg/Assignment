package com.Capstone.InterviewTracking.exception;

import com.Capstone.InterviewTracking.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleBadRequest_returns409Conflict() {
        BadRequestException ex = new BadRequestException("Stage error");

        ResponseEntity<ApiResponse<Void>> response = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Stage error", response.getBody().getMessage());
    }

    @Test
    void handleEmailAlreadyRegistered_returns409Conflict() {
        EmailAlreadyRegisteredException ex = new EmailAlreadyRegisteredException("Email already registered");

        ResponseEntity<ApiResponse<Void>> response = handler.handleEmailAlreadyRegistered(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already registered", response.getBody().getMessage());
    }

    @Test
    void handleInvalidCredentials_returns401Unauthorized() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Bad credentials");

        ResponseEntity<ApiResponse<Void>> response = handler.handleInvalidCredentials(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void handleUserNotFound_returns401Unauthorized() {
        UserNotFoundException ex = new UserNotFoundException("User not found");

        ResponseEntity<ApiResponse<Void>> response = handler.handleInvalidCredentials(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void handleMaxUploadSize_returns413() {
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(5 * 1024 * 1024L);

        ResponseEntity<ApiResponse<Void>> response = handler.handleMaxUploadSize(ex);

        assertEquals(413, response.getStatusCode().value());
    }

    @Test
    void handleEmailSendingError_returns500() {
        EmailSendingException ex = new EmailSendingException("Mail server down");

        ResponseEntity<ApiResponse<Void>> response = handler.handleEmailError(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Mail server down", response.getBody().getMessage());
    }

    @Test
    void handleInvalidRequestBody_returns400() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleInvalidRequestBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleUnexpected_returns500WithMessage() {
        Exception ex = new RuntimeException("Something went wrong");

        ResponseEntity<ApiResponse<Void>> response = handler.handleUnexpected(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Something went wrong", response.getBody().getMessage());
    }

    @Test
    void handleUnexpected_nullMessage_returns500WithFallback() {
        Exception ex = new RuntimeException((String) null);

        ResponseEntity<ApiResponse<Void>> response = handler.handleUnexpected(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Request failed", response.getBody().getMessage());
    }
}
