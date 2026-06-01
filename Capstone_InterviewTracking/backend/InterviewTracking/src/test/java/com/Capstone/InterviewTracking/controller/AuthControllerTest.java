package com.Capstone.InterviewTracking.controller;

import com.Capstone.InterviewTracking.dto.AuthRequest;
import com.Capstone.InterviewTracking.dto.AuthResponse;
import com.Capstone.InterviewTracking.dto.SignupRequest;
import com.Capstone.InterviewTracking.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    AuthService authService;
    private AuthController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new AuthController(authService);
    }

    @Test
    void register_returnsCreated() {
        SignupRequest req = new SignupRequest();
        req.setEmail("test@example.com");
        req.setFullName("Test User");
        req.setPhone("1234567890");
        req.setDob(LocalDate.of(1990, 1, 1));
        req.setGender("Male");

        ResponseEntity<?> response = controller.register(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authService).register(req);
    }

    @Test
    void setPassword_returnsOk() {
        Map<String, String> body = Map.of("token", "tok123", "password", "pass123");

        ResponseEntity<?> response = controller.setPassword(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).setPassword("tok123", "pass123");
    }

    @Test
    void login_returnsOkWithAuthResponse() {
        AuthRequest req = new AuthRequest();
        req.setEmail("hr@example.com");
        req.setPassword("secret");

        AuthResponse authResp = new AuthResponse("tok", "hr@example.com", "HR");
        when(authService.login(req)).thenReturn(authResp);

        ResponseEntity<?> response = controller.login(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).login(req);
    }
}
