package com.Capstone.InterviewTracking.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PanelResponseTest {

    @Test
    void constructorAndSetters() {
        PanelResponse resp = new PanelResponse(1L, "Bob", "bob@example.com", "1234567890", "Corp", "Eng");

        assertEquals(1L, resp.getId());
        assertEquals("Bob", resp.getFullName());
        assertEquals("bob@example.com", resp.getEmail());
        assertEquals("1234567890", resp.getPhone());
        assertEquals("Corp", resp.getOrganization());
        assertEquals("Eng", resp.getDesignation());

        resp.setId(2L);
        resp.setFullName("Carol");
        resp.setEmail("carol@example.com");
        resp.setPhone("0987654321");
        resp.setOrganization("Startup");
        resp.setDesignation("Lead");

        assertEquals(2L, resp.getId());
        assertEquals("Carol", resp.getFullName());
        assertEquals("carol@example.com", resp.getEmail());
        assertEquals("0987654321", resp.getPhone());
        assertEquals("Startup", resp.getOrganization());
        assertEquals("Lead", resp.getDesignation());
    }
}
