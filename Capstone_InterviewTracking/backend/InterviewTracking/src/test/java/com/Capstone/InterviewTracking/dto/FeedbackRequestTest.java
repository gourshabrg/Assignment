package com.Capstone.InterviewTracking.dto;

import com.Capstone.InterviewTracking.enums.FeedbackStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackRequestTest {

    @Test
    void settersAndGetters() {
        FeedbackRequest req = new FeedbackRequest();
        req.setComments("Good candidate");
        req.setStrengths("Problem solving");
        req.setWeaknesses("Communication");
        req.setAreasCovered("Java, Spring");
        req.setRating(4);
        req.setStatus(FeedbackStatus.SELECTED);

        assertEquals("Good candidate", req.getComments());
        assertEquals("Problem solving", req.getStrengths());
        assertEquals("Communication", req.getWeaknesses());
        assertEquals("Java, Spring", req.getAreasCovered());
        assertEquals(4, req.getRating());
        assertEquals(FeedbackStatus.SELECTED, req.getStatus());
    }
}
