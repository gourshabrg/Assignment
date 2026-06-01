package com.Capstone.InterviewTracking.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PanelRequestTest {

    @Test
    void settersAndGetters() {
        PanelRequest req = new PanelRequest();
        req.setFullName("Alice");
        req.setEmail("alice@example.com");
        req.setPhone("9876543210");
        req.setOrganization("TechCorp");
        req.setDesignation("Senior Engineer");

        assertEquals("Alice", req.getFullName());
        assertEquals("alice@example.com", req.getEmail());
        assertEquals("9876543210", req.getPhone());
        assertEquals("TechCorp", req.getOrganization());
        assertEquals("Senior Engineer", req.getDesignation());
    }
}
