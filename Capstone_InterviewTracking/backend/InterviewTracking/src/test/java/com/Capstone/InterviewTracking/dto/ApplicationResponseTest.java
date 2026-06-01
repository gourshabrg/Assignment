package com.Capstone.InterviewTracking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationResponseTest {

    @Test
    void getters() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse resp = new ApplicationResponse(1L, 2L, "Java Dev",
                "APPLIED", "PROFILING", "http://resume.pdf", now);

        assertEquals(1L, resp.getApplicationId());
        assertEquals(2L, resp.getJobId());
        assertEquals("Java Dev", resp.getJobTitle());
        assertEquals("APPLIED", resp.getStatus());
        assertEquals("PROFILING", resp.getStage());
        assertEquals("http://resume.pdf", resp.getResumeUrl());
        assertEquals(now, resp.getAppliedAt());
    }
}
