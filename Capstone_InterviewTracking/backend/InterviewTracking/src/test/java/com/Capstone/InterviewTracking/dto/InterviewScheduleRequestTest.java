package com.Capstone.InterviewTracking.dto;

import com.Capstone.InterviewTracking.enums.InterviewRound;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InterviewScheduleRequestTest {

    @Test
    void settersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        InterviewScheduleRequest req = new InterviewScheduleRequest();
        req.setApplicationId(1L);
        req.setRound(InterviewRound.L1);
        req.setInterviewDateTime(now);
        req.setFocusArea("Algorithms");
        req.setPanelIds(List.of(1L, 2L));

        assertEquals(1L, req.getApplicationId());
        assertEquals(InterviewRound.L1, req.getRound());
        assertEquals(now, req.getInterviewDateTime());
        assertEquals("Algorithms", req.getFocusArea());
        assertEquals(2, req.getPanelIds().size());
    }
}
