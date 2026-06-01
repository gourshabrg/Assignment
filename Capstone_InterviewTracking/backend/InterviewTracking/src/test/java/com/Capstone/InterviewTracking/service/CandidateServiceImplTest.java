package com.Capstone.InterviewTracking.service;

import com.Capstone.InterviewTracking.dto.CandidateDetailResponse;
import com.Capstone.InterviewTracking.entity.Application;
import com.Capstone.InterviewTracking.entity.Candidate;
import com.Capstone.InterviewTracking.entity.JobDescription;
import com.Capstone.InterviewTracking.enums.ApplicationStatus;
import com.Capstone.InterviewTracking.enums.InterviewStage;
import com.Capstone.InterviewTracking.enums.JobType;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import com.Capstone.InterviewTracking.repository.ApplicationRepository;
import com.Capstone.InterviewTracking.repository.CandidateRepository;
import com.Capstone.InterviewTracking.repository.InterviewRepository;
import com.Capstone.InterviewTracking.repository.JobDescriptionRepository;
import com.Capstone.InterviewTracking.repository.UserRepository;
import com.Capstone.InterviewTracking.service.impl.CandidateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceImplTest {

    @Mock private CandidateRepository candidateRepository;
    @Mock private JobDescriptionRepository jobRepository;
    @Mock private DriveService driveService;
    @Mock private AuthService authService;
    @Mock private ApplicationRepository applicationRepository;
    @Mock private UserRepository userRepository;
    @Mock private InterviewRepository interviewRepository;

    @InjectMocks
    private CandidateServiceImpl service;

    private Candidate buildCandidate() {
        Candidate c = new Candidate();
        c.setId(1L);
        c.setEmail("candidate@example.com");
        c.setFullName("Jane Doe");
        c.setMobile("9876543210");
        c.setCurrentCompany("TechCorp");
        c.setTotalExperience(3.0);
        c.setRelevantExperience(2.0);
        c.setCurrentCtc(6.0);
        c.setExpectedCtc(10.0);
        c.setNoticePeriod(30);
        c.setPreferredLocation("Mumbai");
        c.setSource("LinkedIn");
        c.setResumeUrl("https://drive.google.com/resume.pdf");
        return c;
    }

    private JobDescription buildJob() {
        JobDescription jd = new JobDescription();
        jd.setId(100L);
        jd.setTitle("Java Developer");
        jd.setDescription("Backend role");
        jd.setSkills("Java, Spring");
        jd.setLocation("Mumbai");
        jd.setJobType(JobType.FULL_TIME);
        jd.setActive(true);
        return jd;
    }

    private Application buildApplication(Candidate candidate, JobDescription jd) {
        Application a = new Application();
        ReflectionTestUtils.setField(a, "id", 10L);
        a.setCandidate(candidate);
        a.setJob(jd);
        a.setStage(InterviewStage.PROFILING);
        a.setStatus(ApplicationStatus.APPLIED);
        return a;
    }

    // ── getMyApplication ─────────────────────────────────────────────────────────

    @Test
    void getMyApplication_validEmail_returnsDetailResponse() {
        Candidate candidate = buildCandidate();
        JobDescription jd = buildJob();
        Application app = buildApplication(candidate, jd);

        when(candidateRepository.findByEmail("candidate@example.com")).thenReturn(Optional.of(candidate));
        when(applicationRepository.findByCandidate(candidate)).thenReturn(Optional.of(app));
        when(interviewRepository.findByCandidate(candidate)).thenReturn(List.of());

        CandidateDetailResponse result = service.getMyApplication("candidate@example.com");

        assertNotNull(result);
        assertEquals("Jane Doe", result.getFullName());
        assertEquals("candidate@example.com", result.getEmail());
        assertEquals("Java Developer", result.getJobTitle());
        assertEquals("PROFILING", result.getStage());
        assertEquals("APPLIED", result.getStatus());
    }

    @Test
    void getMyApplication_candidateNotFound_throwsBadRequestException() {
        when(candidateRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> service.getMyApplication("nobody@example.com"));
    }

    @Test
    void getMyApplication_applicationNotFound_throwsBadRequestException() {
        Candidate candidate = buildCandidate();

        when(candidateRepository.findByEmail("candidate@example.com")).thenReturn(Optional.of(candidate));
        when(applicationRepository.findByCandidate(candidate)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> service.getMyApplication("candidate@example.com"));
    }
}
