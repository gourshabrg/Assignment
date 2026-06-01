package com.Capstone.InterviewTracking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InterviewResponseTest {

    @Test
    void settersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        InterviewResponse resp = new InterviewResponse();
        resp.setId(1L);
        resp.setRound("L1");
        resp.setInterviewDateTime(now);
        resp.setFocusArea("Data Structures");
        resp.setStatus("SCHEDULED");
        resp.setPanels(List.of());
        resp.setApplicationId(1L);
        resp.setCandidateName("John");
        resp.setCandidateEmail("john@example.com");

        assertEquals(1L, resp.getId());
        assertEquals("L1", resp.getRound());
        assertEquals(now, resp.getInterviewDateTime());
        assertEquals("Data Structures", resp.getFocusArea());
        assertEquals("SCHEDULED", resp.getStatus());
        assertNotNull(resp.getPanels());
        assertEquals(1L, resp.getApplicationId());
        assertEquals("John", resp.getCandidateName());
        assertEquals("john@example.com", resp.getCandidateEmail());
    }
}
