package com.Capstone.InterviewTracking.mapper;

import com.Capstone.InterviewTracking.dto.JobDescriptionRequest;
import com.Capstone.InterviewTracking.dto.JobDescriptionResponse;
import com.Capstone.InterviewTracking.entity.JobDescription;
import com.Capstone.InterviewTracking.entity.User;
import com.Capstone.InterviewTracking.enums.JobType;
import com.Capstone.InterviewTracking.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobDescriptionMapperTest {

    private JobDescriptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new JobDescriptionMapper();
    }

    private JobDescriptionRequest buildRequest() {
        JobDescriptionRequest r = new JobDescriptionRequest();
        r.setTitle("  Java Developer  ");
        r.setDescription("  Backend role  ");
        r.setSkills("  Java, Spring  ");
        r.setLocation("  Mumbai  ");
        r.setMinSalary(5.0);
        r.setMaxSalary(15.0);
        r.setMinExperience(1);
        r.setMaxExperience(5);
        r.setJobType(JobType.FULL_TIME);
        return r;
    }

    private User buildHrUser() {
        User u = new User();
        u.setEmail("hr@example.com");
        u.setRole(RoleType.HR);
        return u;
    }

    // ── toEntity ─────────────────────────────────────────────────────────────────

    @Test
    void toEntity_mapsAllFieldsAndTrimsStrings() {
        JobDescriptionRequest req = buildRequest();
        User hr = buildHrUser();

        JobDescription result = mapper.toEntity(req, hr);

        assertEquals("Java Developer", result.getTitle());
        assertEquals("Backend role", result.getDescription());
        assertEquals("Java, Spring", result.getSkills());
        assertEquals("Mumbai", result.getLocation());
        assertEquals(5.0, result.getMinSalary());
        assertEquals(15.0, result.getMaxSalary());
        assertEquals(1, result.getMinExperience());
        assertEquals(5, result.getMaxExperience());
        assertEquals(JobType.FULL_TIME, result.getJobType());
        assertTrue(result.isActive());
        assertEquals(hr, result.getCreatedBy());
    }

    // ── toResponse ────────────────────────────────────────────────────────────────

    @Test
    void toResponse_mapsAllFields() {
        JobDescription jd = new JobDescription();
        jd.setId(1L);
        jd.setTitle("Java Developer");
        jd.setDescription("Backend role");
        jd.setSkills("Java, Spring");
        jd.setLocation("Mumbai");
        jd.setMinSalary(5.0);
        jd.setMaxSalary(15.0);
        jd.setMinExperience(1);
        jd.setMaxExperience(5);
        jd.setJobType(JobType.FULL_TIME);
        jd.setActive(true);

        JobDescriptionResponse result = mapper.toResponse(jd);

        assertEquals(1L, result.getId());
        assertEquals("Java Developer", result.getTitle());
        assertEquals("Backend role", result.getDescription());
        assertEquals("Java, Spring", result.getSkills());
        assertEquals("Mumbai", result.getLocation());
        assertEquals(5.0, result.getMinSalary());
        assertEquals(15.0, result.getMaxSalary());
        assertEquals(1, result.getMinExperience());
        assertEquals(5, result.getMaxExperience());
        assertEquals(JobType.FULL_TIME, result.getJobType());
        assertTrue(result.isActive());
    }
}
