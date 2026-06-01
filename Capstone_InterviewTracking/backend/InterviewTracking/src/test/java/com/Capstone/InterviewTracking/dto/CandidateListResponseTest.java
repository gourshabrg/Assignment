package com.Capstone.InterviewTracking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CandidateListResponseTest {

    @Test
    void settersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        CandidateListResponse r = new CandidateListResponse();
        r.setApplicationId(1L);
        r.setCandidateId(2L);
        r.setFullName("Jane Doe");
        r.setEmail("jane@example.com");
        r.setMobile("9876543210");
        r.setJobId(3L);
        r.setJobTitle("Backend Dev");
        r.setStage("L1");
        r.setStatus("APPLIED");
        r.setAppliedAt(now);

        assertEquals(1L, r.getApplicationId());
        assertEquals(2L, r.getCandidateId());
        assertEquals("Jane Doe", r.getFullName());
        assertEquals("jane@example.com", r.getEmail());
        assertEquals("9876543210", r.getMobile());
        assertEquals(3L, r.getJobId());
        assertEquals("Backend Dev", r.getJobTitle());
        assertEquals("L1", r.getStage());
        assertEquals("APPLIED", r.getStatus());
        assertEquals(now, r.getAppliedAt());
    }
}
