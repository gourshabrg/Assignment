package com.Capstone.InterviewTracking.dto;

import com.Capstone.InterviewTracking.enums.JobType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobDescriptionRequestTest {

    @Test
    void settersAndGetters() {
        JobDescriptionRequest req = new JobDescriptionRequest();
        req.setTitle("Java Dev");
        req.setDescription("Backend role");
        req.setSkills("Java, Spring");
        req.setLocation("Bangalore");
        req.setMinSalary(10.0);
        req.setMaxSalary(20.0);
        req.setMinExperience(2);
        req.setMaxExperience(5);
        req.setJobType(JobType.FULL_TIME);

        assertEquals("Java Dev", req.getTitle());
        assertEquals("Backend role", req.getDescription());
        assertEquals("Java, Spring", req.getSkills());
        assertEquals("Bangalore", req.getLocation());
        assertEquals(10.0, req.getMinSalary());
        assertEquals(20.0, req.getMaxSalary());
        assertEquals(2, req.getMinExperience());
        assertEquals(5, req.getMaxExperience());
        assertEquals(JobType.FULL_TIME, req.getJobType());
    }
}
