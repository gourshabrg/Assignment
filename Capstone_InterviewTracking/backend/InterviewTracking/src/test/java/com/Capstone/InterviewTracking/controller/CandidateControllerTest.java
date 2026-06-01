package com.Capstone.InterviewTracking.controller;

import com.Capstone.InterviewTracking.dto.ApplicationResponse;
import com.Capstone.InterviewTracking.dto.CandidateDetailResponse;
import com.Capstone.InterviewTracking.service.CandidateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CandidateControllerTest {

    @Mock
    CandidateService candidateService;
    @Mock
    Authentication authentication;
    private CandidateController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new CandidateController(candidateService);
        when(authentication.getName()).thenReturn("candidate@example.com");
    }

    private MultipartFile mockResume() {
        return new MockMultipartFile("resume", "resume.pdf", "application/pdf", "content".getBytes());
    }

    @Test
    void apply_returnsCreated() {
        ApplicationResponse resp = new ApplicationResponse(1L, 1L, "Java Dev",
                "APPLIED", "PROFILING", "url", LocalDateTime.now());
        when(candidateService.applyCandidate(any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(resp);

        ResponseEntity<?> result = controller.apply("John", "j@example.com", "9876543210",
                null, mockResume(), null, null, null, null, null, null, null, null, 1L);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void createByHR_returnsCreated() {
        ApplicationResponse resp = new ApplicationResponse(1L, 1L, "Java Dev",
                "APPLIED", "PROFILING", "url", LocalDateTime.now());
        when(candidateService.createByHR(any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(resp);

        ResponseEntity<?> result = controller.createByHR("John", "j@example.com", "9876543210",
                null, mockResume(), null, null, null, null, null, null, null, null, 1L, authentication);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void getMyApplication_returnsOk() {
        CandidateDetailResponse detail = new CandidateDetailResponse();
        when(candidateService.getMyApplication("candidate@example.com")).thenReturn(detail);

        ResponseEntity<?> result = controller.getMyApplication(authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
