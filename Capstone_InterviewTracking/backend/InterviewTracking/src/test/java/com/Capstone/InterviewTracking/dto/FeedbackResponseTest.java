package com.Capstone.InterviewTracking.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackResponseTest {

    @Test
    void settersAndGetters() {
        FeedbackResponse resp = new FeedbackResponse();
        resp.setId(1L);
        resp.setInterviewId(2L);
        resp.setRound("L1");
        resp.setPanelName("Alice");
        resp.setComments("Excellent");
        resp.setStrengths("Java expertise");
        resp.setWeaknesses("None");
        resp.setAreasCovered("Core Java");
        resp.setRating(5);
        resp.setStatus("SELECTED");

        assertEquals(1L, resp.getId());
        assertEquals(2L, resp.getInterviewId());
        assertEquals("L1", resp.getRound());
        assertEquals("Alice", resp.getPanelName());
        assertEquals("Excellent", resp.getComments());
        assertEquals("Java expertise", resp.getStrengths());
        assertEquals("None", resp.getWeaknesses());
        assertEquals("Core Java", resp.getAreasCovered());
        assertEquals(5, resp.getRating());
        assertEquals("SELECTED", resp.getStatus());
    }
}
