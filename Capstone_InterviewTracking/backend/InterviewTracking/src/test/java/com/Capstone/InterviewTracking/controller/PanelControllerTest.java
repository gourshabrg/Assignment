package com.Capstone.InterviewTracking.controller;

import com.Capstone.InterviewTracking.dto.FeedbackRequest;
import com.Capstone.InterviewTracking.dto.FeedbackResponse;
import com.Capstone.InterviewTracking.dto.InterviewResponse;
import com.Capstone.InterviewTracking.entity.*;
import com.Capstone.InterviewTracking.enums.*;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import com.Capstone.InterviewTracking.repository.*;
import com.Capstone.InterviewTracking.service.*;
import com.Capstone.InterviewTracking.service.impl.InterviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PanelControllerTest {

    @Mock InterviewService interviewService;
    @Mock InterviewServiceImpl interviewServiceImpl;
    @Mock FeedbackService feedbackService;
    @Mock InterviewRepository interviewRepository;
    @Mock PanelRepository panelRepository;
    @Mock ApplicationRepository applicationRepository;
    @Mock Authentication authentication;

    private PanelController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new PanelController(interviewService, interviewServiceImpl, feedbackService,
                interviewRepository, panelRepository, applicationRepository);
        when(authentication.getName()).thenReturn("panel@example.com");
    }

    @Test
    void getMyInterviews_returnsOk() {
        when(interviewService.getInterviewsByPanel("panel@example.com")).thenReturn(List.of());

        ResponseEntity<?> result = controller.getMyInterviews(authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getCandidateForInterview_interviewNotFound_throwsBadRequest() {
        when(interviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> controller.getCandidateForInterview(99L, authentication));
    }

    @Test
    void getCandidateForInterview_panelNotFound_throwsBadRequest() {
        Interview interview = mock(Interview.class);
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(panelRepository.findByEmail("panel@example.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> controller.getCandidateForInterview(1L, authentication));
    }

    @Test
    void getCandidateForInterview_notAssigned_throwsBadRequest() {
        Panel panel = mock(Panel.class);
        when(panel.getId()).thenReturn(1L);

        Interview interview = mock(Interview.class);
        when(interview.getPanels()).thenReturn(List.of());
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(panelRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(panel));

        assertThrows(BadRequestException.class,
                () -> controller.getCandidateForInterview(1L, authentication));
    }

    @Test
    void getCandidateForInterview_noApplication_throwsBadRequest() {
        Panel panel = mock(Panel.class);
        when(panel.getId()).thenReturn(1L);

        Candidate candidate = mock(Candidate.class);
        Interview interview = mock(Interview.class);
        when(interview.getCandidate()).thenReturn(candidate);
        when(interview.getPanels()).thenReturn(List.of(panel));
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(panelRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(panel));
        when(applicationRepository.findByCandidateOrderByCreatedAtDesc(candidate)).thenReturn(List.of());

        assertThrows(BadRequestException.class,
                () -> controller.getCandidateForInterview(1L, authentication));
    }

    @Test
    void getCandidateForInterview_success_returnsOk() {
        Panel panel = mock(Panel.class);
        when(panel.getId()).thenReturn(1L);

        Candidate candidate = mock(Candidate.class);
        when(candidate.getId()).thenReturn(1L);
        when(candidate.getFullName()).thenReturn("John");
        when(candidate.getEmail()).thenReturn("john@example.com");
        when(candidate.getMobile()).thenReturn("9876543210");
        when(candidate.getCurrentCompany()).thenReturn("Corp");
        when(candidate.getTotalExperience()).thenReturn(3.0);
        when(candidate.getRelevantExperience()).thenReturn(2.0);
        when(candidate.getCurrentCtc()).thenReturn(8.0);
        when(candidate.getExpectedCtc()).thenReturn(12.0);
        when(candidate.getNoticePeriod()).thenReturn(30);
        when(candidate.getPreferredLocation()).thenReturn("Bangalore");
        when(candidate.getSource()).thenReturn("Naukri");
        when(candidate.getResumeUrl()).thenReturn("http://resume.pdf");

        JobDescription jd = mock(JobDescription.class);
        when(jd.getId()).thenReturn(1L);
        when(jd.getTitle()).thenReturn("Java Dev");
        when(jd.getDescription()).thenReturn("Backend Java");
        when(jd.getSkills()).thenReturn("Java, Spring");
        when(jd.getLocation()).thenReturn("Bangalore");
        when(jd.getJobType()).thenReturn(JobType.FULL_TIME);

        Application app = mock(Application.class);
        when(app.getId()).thenReturn(1L);
        when(app.getCandidate()).thenReturn(candidate);
        when(app.getJob()).thenReturn(jd);
        when(app.getStage()).thenReturn(InterviewStage.L1);
        when(app.getStatus()).thenReturn(ApplicationStatus.APPLIED);
        when(app.getCreatedAt()).thenReturn(LocalDateTime.now());

        Interview interview = mock(Interview.class);
        when(interview.getCandidate()).thenReturn(candidate);
        when(interview.getPanels()).thenReturn(List.of(panel));

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(panelRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(panel));
        when(applicationRepository.findByCandidateOrderByCreatedAtDesc(candidate)).thenReturn(List.of(app));
        when(interviewRepository.findByCandidate(candidate)).thenReturn(List.of());
        when(interviewServiceImpl.toResponse(any())).thenReturn(new InterviewResponse());

        ResponseEntity<?> result = controller.getCandidateForInterview(1L, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void submitFeedback_returnsCreated() {
        FeedbackRequest req = new FeedbackRequest();
        req.setComments("Good");
        req.setAreasCovered("Java");
        req.setRating(4);
        req.setStatus(FeedbackStatus.SELECTED);

        when(feedbackService.submitFeedback(eq(1L), eq("panel@example.com"), any()))
                .thenReturn(new FeedbackResponse());

        ResponseEntity<?> result = controller.submitFeedback(1L, req, authentication);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }
}
