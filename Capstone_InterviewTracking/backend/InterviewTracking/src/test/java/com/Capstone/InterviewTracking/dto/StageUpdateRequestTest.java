package com.Capstone.InterviewTracking.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StageUpdateRequestTest {

    @Test
    void settersAndGetters() {
        StageUpdateRequest req = new StageUpdateRequest();
        req.setStage("L1");
        req.setComments("Moving to L1");

        assertEquals("L1", req.getStage());
        assertEquals("Moving to L1", req.getComments());
    }
}
