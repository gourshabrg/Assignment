package com.Capstone.InterviewTracking.dto;

import com.Capstone.InterviewTracking.enums.JobType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JobDescriptionResponseTest {

    @Test
    void settersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        JobDescriptionResponse resp = new JobDescriptionResponse();
        resp.setId(1L);
        resp.setTitle("Java Dev");
        resp.setDescription("Backend");
        resp.setSkills("Java");
        resp.setLocation("Bangalore");
        resp.setMinSalary(10.0);
        resp.setMaxSalary(20.0);
        resp.setMinExperience(2);
        resp.setMaxExperience(5);
        resp.setJobType(JobType.CONTRACT);
        resp.setActive(true);
        resp.setHasApplications(false);
        resp.setCreatedAt(now);

        assertEquals(1L, resp.getId());
        assertEquals("Java Dev", resp.getTitle());
        assertEquals("Backend", resp.getDescription());
        assertEquals("Java", resp.getSkills());
        assertEquals("Bangalore", resp.getLocation());
        assertEquals(10.0, resp.getMinSalary());
        assertEquals(20.0, resp.getMaxSalary());
        assertEquals(2, resp.getMinExperience());
        assertEquals(5, resp.getMaxExperience());
        assertEquals(JobType.CONTRACT, resp.getJobType());
        assertTrue(resp.isActive());
        assertFalse(resp.isHasApplications());
        assertEquals(now, resp.getCreatedAt());
    }
}
