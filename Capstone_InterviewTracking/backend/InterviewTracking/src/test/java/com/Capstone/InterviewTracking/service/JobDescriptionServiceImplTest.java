package com.Capstone.InterviewTracking.service;

import com.Capstone.InterviewTracking.dto.JobDescriptionRequest;
import com.Capstone.InterviewTracking.dto.JobDescriptionResponse;
import com.Capstone.InterviewTracking.entity.JobDescription;
import com.Capstone.InterviewTracking.entity.User;
import com.Capstone.InterviewTracking.enums.ApplicationStatus;
import com.Capstone.InterviewTracking.enums.RoleType;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import com.Capstone.InterviewTracking.exception.UserNotFoundException;
import com.Capstone.InterviewTracking.mapper.JobDescriptionMapper;
import com.Capstone.InterviewTracking.repository.ApplicationRepository;
import com.Capstone.InterviewTracking.repository.JobDescriptionRepository;
import com.Capstone.InterviewTracking.repository.UserRepository;
import com.Capstone.InterviewTracking.service.impl.JobDescriptionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobDescriptionServiceImplTest {

    @Mock private JobDescriptionRepository jobDescriptionRepository;
    @Mock private UserRepository userRepository;
    @Mock private JobDescriptionMapper jobDescriptionMapper;
    @Mock private ApplicationRepository applicationRepository;

    @InjectMocks
    private JobDescriptionServiceImpl service;

    private User hrUser() {
        User u = new User();
        u.setEmail("hr@example.com");
        u.setRole(RoleType.HR);
        return u;
    }

    private User candidateUser() {
        User u = new User();
        u.setEmail("candidate@example.com");
        u.setRole(RoleType.CANDIDATE);
        return u;
    }

    private JobDescriptionRequest validRequest() {
        JobDescriptionRequest r = new JobDescriptionRequest();
        r.setTitle("Java Developer");
        r.setDescription("Backend role");
        r.setSkills("Java, Spring");
        r.setLocation("Mumbai");
        r.setMinExperience(1);
        r.setMaxExperience(5);
        r.setMinSalary(5.0);
        r.setMaxSalary(15.0);
        return r;
    }

    private JobDescription savedJob() {
        JobDescription jd = new JobDescription();
        jd.setTitle("Java Developer");
        jd.setActive(true);
        return jd;
    }

    // ── create ──────────────────────────────────────────────────────────────────

    @Test
    void create_validRequest_returnsResponse() {
        JobDescriptionRequest req = validRequest();
        JobDescription jd = savedJob();
        JobDescriptionResponse resp = new JobDescriptionResponse();

        when(userRepository.findByEmail("hr@example.com")).thenReturn(Optional.of(hrUser()));
        when(jobDescriptionMapper.toEntity(eq(req), any(User.class))).thenReturn(jd);
        when(jobDescriptionRepository.save(jd)).thenReturn(jd);
        when(jobDescriptionMapper.toResponse(jd)).thenReturn(resp);

        JobDescriptionResponse result = service.create(req, "hr@example.com");

        assertNotNull(result);
        verify(jobDescriptionRepository).save(jd);
    }

    @Test
    void create_userNotFound_throwsUserNotFoundException() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.create(validRequest(), "unknown@example.com"));
    }

    @Test
    void create_invalidExperienceRange_throwsBadRequestException() {
        JobDescriptionRequest req = validRequest();
        req.setMinExperience(10);
        req.setMaxExperience(2);

        assertThrows(BadRequestException.class, () -> service.create(req, "hr@example.com"));
    }

    @Test
    void create_invalidSalaryRange_throwsBadRequestException() {
        JobDescriptionRequest req = validRequest();
        req.setMinSalary(20.0);
        req.setMaxSalary(10.0);

        assertThrows(BadRequestException.class, () -> service.create(req, "hr@example.com"));
    }

    // ── findActiveJobs ──────────────────────────────────────────────────────────

    @Test
    void findActiveJobs_returnsListOfResponses() {
        JobDescription jd = savedJob();
        JobDescriptionResponse resp = new JobDescriptionResponse();

        when(jobDescriptionRepository.findByActiveTrueOrderByCreatedAtDesc()).thenReturn(List.of(jd));
        when(jobDescriptionMapper.toResponse(jd)).thenReturn(resp);

        List<JobDescriptionResponse> result = service.findActiveJobs();

        assertEquals(1, result.size());
    }

    @Test
    void findActiveJobs_noJobs_returnsEmptyList() {
        when(jobDescriptionRepository.findByActiveTrueOrderByCreatedAtDesc()).thenReturn(List.of());

        assertTrue(service.findActiveJobs().isEmpty());
    }

    // ── update ──────────────────────────────────────────────────────────────────

    @Test
    void update_validHrUser_returnsUpdatedResponse() {
        JobDescriptionRequest req = validRequest();
        JobDescription jd = savedJob();
        JobDescriptionResponse resp = new JobDescriptionResponse();

        when(userRepository.findByEmail("hr@example.com")).thenReturn(Optional.of(hrUser()));
        when(jobDescriptionRepository.findById(1L)).thenReturn(Optional.of(jd));
        when(jobDescriptionRepository.save(jd)).thenReturn(jd);
        when(jobDescriptionMapper.toResponse(jd)).thenReturn(resp);

        JobDescriptionResponse result = service.update(1L, req, "hr@example.com");

        assertNotNull(result);
        verify(jobDescriptionRepository).save(jd);
    }

    @Test
    void update_nonHrUser_throwsBadRequestException() {
        when(userRepository.findByEmail("candidate@example.com")).thenReturn(Optional.of(candidateUser()));

        assertThrows(BadRequestException.class,
                () -> service.update(1L, validRequest(), "candidate@example.com"));
    }

    @Test
    void update_jobNotFound_throwsBadRequestException() {
        when(userRepository.findByEmail("hr@example.com")).thenReturn(Optional.of(hrUser()));
        when(jobDescriptionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> service.update(99L, validRequest(), "hr@example.com"));
    }

    // ── delete ──────────────────────────────────────────────────────────────────

    @Test
    void delete_noActiveApplications_deletesJob() {
        JobDescription jd = savedJob();

        when(userRepository.findByEmail("hr@example.com")).thenReturn(Optional.of(hrUser()));
        when(jobDescriptionRepository.findById(1L)).thenReturn(Optional.of(jd));
        when(applicationRepository.existsByJobAndStatusNot(jd, ApplicationStatus.REJECTED)).thenReturn(false);

        service.delete(1L, "hr@example.com");

        verify(jobDescriptionRepository).delete(jd);
    }

    @Test
    void delete_hasActiveApplications_throwsBadRequestException() {
        JobDescription jd = savedJob();

        when(userRepository.findByEmail("hr@example.com")).thenReturn(Optional.of(hrUser()));
        when(jobDescriptionRepository.findById(1L)).thenReturn(Optional.of(jd));
        when(applicationRepository.existsByJobAndStatusNot(jd, ApplicationStatus.REJECTED)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.delete(1L, "hr@example.com"));
        verify(jobDescriptionRepository, never()).delete(any());
    }

    @Test
    void delete_nonHrUser_throwsBadRequestException() {
        when(userRepository.findByEmail("candidate@example.com")).thenReturn(Optional.of(candidateUser()));

        assertThrows(BadRequestException.class,
                () -> service.delete(1L, "candidate@example.com"));
    }

    // ── getById ─────────────────────────────────────────────────────────────────

    @Test
    void getById_existingJob_returnsResponse() {
        JobDescription jd = savedJob();
        JobDescriptionResponse resp = new JobDescriptionResponse();

        when(jobDescriptionRepository.findById(1L)).thenReturn(Optional.of(jd));
        when(jobDescriptionMapper.toResponse(jd)).thenReturn(resp);
        when(applicationRepository.existsByJob(jd)).thenReturn(false);

        JobDescriptionResponse result = service.getById(1L);

        assertNotNull(result);
    }

    @Test
    void getById_notFound_throwsBadRequestException() {
        when(jobDescriptionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> service.getById(99L));
    }

    // ── findAllForHR ─────────────────────────────────────────────────────────────

    @Test
    void findAllForHR_returnsListWithHasApplicationsFlag() {
        JobDescription jd = savedJob();
        JobDescriptionResponse resp = new JobDescriptionResponse();

        when(jobDescriptionRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(jd));
        when(jobDescriptionMapper.toResponse(jd)).thenReturn(resp);
        when(applicationRepository.existsByJob(jd)).thenReturn(true);

        List<JobDescriptionResponse> result = service.findAllForHR();

        assertEquals(1, result.size());
        verify(applicationRepository).existsByJob(jd);
    }

    // ── toggleActive ─────────────────────────────────────────────────────────────

    @Test
    void toggleActive_hrUser_togglesJobStatus() {
        JobDescription jd = savedJob();
        jd.setActive(true);

        when(userRepository.findByEmail("hr@example.com")).thenReturn(Optional.of(hrUser()));
        when(jobDescriptionRepository.findById(1L)).thenReturn(Optional.of(jd));
        when(jobDescriptionRepository.save(jd)).thenReturn(jd);

        service.toggleActive(1L, "hr@example.com");

        assertFalse(jd.isActive());
        verify(jobDescriptionRepository).save(jd);
    }

    @Test
    void toggleActive_nonHrUser_throwsBadRequestException() {
        when(userRepository.findByEmail("candidate@example.com")).thenReturn(Optional.of(candidateUser()));

        assertThrows(BadRequestException.class,
                () -> service.toggleActive(1L, "candidate@example.com"));
    }
}
