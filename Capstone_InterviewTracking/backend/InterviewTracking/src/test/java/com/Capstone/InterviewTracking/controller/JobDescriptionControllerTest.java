package com.Capstone.InterviewTracking.controller;

import com.Capstone.InterviewTracking.dto.JobDescriptionRequest;
import com.Capstone.InterviewTracking.dto.JobDescriptionResponse;
import com.Capstone.InterviewTracking.enums.JobType;
import com.Capstone.InterviewTracking.service.JobDescriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class JobDescriptionControllerTest {

    @Mock
    JobDescriptionService jobDescriptionService;
    @Mock
    Authentication authentication;
    private JobDescriptionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new JobDescriptionController(jobDescriptionService);
        when(authentication.getName()).thenReturn("hr@example.com");
    }

    private JobDescriptionRequest buildRequest() {
        JobDescriptionRequest req = new JobDescriptionRequest();
        req.setTitle("Java Dev");
        req.setDescription("Java backend role");
        req.setSkills("Java, Spring");
        req.setLocation("Bangalore");
        req.setJobType(JobType.FULL_TIME);
        return req;
    }

    @Test
    void create_returnsCreated() {
        when(jobDescriptionService.create(any(), eq("hr@example.com")))
                .thenReturn(new JobDescriptionResponse());

        ResponseEntity<?> result = controller.create(buildRequest(), authentication);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void findActiveJobs_returnsOk() {
        when(jobDescriptionService.findActiveJobs()).thenReturn(List.of());

        ResponseEntity<?> result = controller.findActiveJobs();

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void update_returnsOk() {
        when(jobDescriptionService.update(eq(1L), any(), eq("hr@example.com")))
                .thenReturn(new JobDescriptionResponse());

        ResponseEntity<?> result = controller.update(1L, buildRequest(), authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void delete_returnsOk() {
        ResponseEntity<?> result = controller.delete(1L, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(jobDescriptionService).delete(1L, "hr@example.com");
    }

    @Test
    void getById_returnsOk() {
        when(jobDescriptionService.getById(1L)).thenReturn(new JobDescriptionResponse());

        ResponseEntity<?> result = controller.getById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAllForHR_returnsOk() {
        when(jobDescriptionService.findAllForHR()).thenReturn(List.of());

        ResponseEntity<?> result = controller.getAllForHR();

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void toggleJob_returnsOk() {
        ResponseEntity<?> result = controller.toggleJob(1L, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(jobDescriptionService).toggleActive(1L, "hr@example.com");
    }
}
