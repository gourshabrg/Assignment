package com.Capstone.InterviewTracking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CandidateDetailResponseTest {

    @Test
    void allSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        CandidateDetailResponse r = new CandidateDetailResponse();
        r.setApplicationId(1L);
        r.setCandidateId(2L);
        r.setFullName("John Doe");
        r.setEmail("john@example.com");
        r.setMobile("9876543210");
        r.setCurrentCompany("Corp");
        r.setTotalExperience(5.0);
        r.setRelevantExperience(3.0);
        r.setCurrentCtc(10.0);
        r.setExpectedCtc(15.0);
        r.setNoticePeriod(30);
        r.setPreferredLocation("Bangalore");
        r.setSource("LinkedIn");
        r.setResumeUrl("http://resume.pdf");
        r.setJobId(1L);
        r.setJobTitle("Java Dev");
        r.setJobDescription("Backend");
        r.setSkills("Java");
        r.setLocation("Bangalore");
        r.setJobType("FULL_TIME");
        r.setStage("PROFILING");
        r.setStatus("APPLIED");
        r.setAppliedAt(now);
        r.setInterviews(List.of());
        r.setFeedbacks(List.of());

        assertEquals(1L, r.getApplicationId());
        assertEquals(2L, r.getCandidateId());
        assertEquals("John Doe", r.getFullName());
        assertEquals("john@example.com", r.getEmail());
        assertEquals("9876543210", r.getMobile());
        assertEquals("Corp", r.getCurrentCompany());
        assertEquals(5.0, r.getTotalExperience());
        assertEquals(3.0, r.getRelevantExperience());
        assertEquals(10.0, r.getCurrentCtc());
        assertEquals(15.0, r.getExpectedCtc());
        assertEquals(30, r.getNoticePeriod());
        assertEquals("Bangalore", r.getPreferredLocation());
        assertEquals("LinkedIn", r.getSource());
        assertEquals("http://resume.pdf", r.getResumeUrl());
        assertEquals(1L, r.getJobId());
        assertEquals("Java Dev", r.getJobTitle());
        assertEquals("Backend", r.getJobDescription());
        assertEquals("Java", r.getSkills());
        assertEquals("Bangalore", r.getLocation());
        assertEquals("FULL_TIME", r.getJobType());
        assertEquals("PROFILING", r.getStage());
        assertEquals("APPLIED", r.getStatus());
        assertEquals(now, r.getAppliedAt());
        assertNotNull(r.getInterviews());
        assertNotNull(r.getFeedbacks());
    }
}
