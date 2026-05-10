package com.Capstone.InterviewTracking.controller;

import com.Capstone.InterviewTracking.dto.*;
import com.Capstone.InterviewTracking.entity.*;
import com.Capstone.InterviewTracking.enums.*;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import com.Capstone.InterviewTracking.repository.*;
import com.Capstone.InterviewTracking.service.*;
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

class HrControllerTest {

    @Mock PanelService panelService;
    @Mock ApplicationRepository applicationRepository;
    @Mock PanelRepository panelRepository;
    @Mock InterviewService interviewService;
    @Mock FeedbackService feedbackService;
    @Mock InterviewRepository interviewRepository;
    @Mock Authentication authentication;

    private HrController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new HrController(panelService, applicationRepository, panelRepository,
                interviewService, feedbackService, interviewRepository);
        when(authentication.getName()).thenReturn("hr@example.com");
    }

    private Application buildMockApplication() {
        Application app = mock(Application.class);
        Candidate candidate = mock(Candidate.class);
        JobDescription jd = mock(JobDescription.class);

        when(app.getId()).thenReturn(1L);
        when(app.getCandidate()).thenReturn(candidate);
        when(app.getJob()).thenReturn(jd);
        when(app.getStage()).thenReturn(InterviewStage.PROFILING);
        when(app.getStatus()).thenReturn(ApplicationStatus.APPLIED);
        when(app.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(candidate.getId()).thenReturn(1L);
        when(candidate.getFullName()).thenReturn("John Doe");
        when(candidate.getEmail()).thenReturn("john@example.com");
        when(candidate.getMobile()).thenReturn("9876543210");
        when(candidate.getCurrentCompany()).thenReturn("TechCorp");
        when(candidate.getTotalExperience()).thenReturn(5.0);
        when(candidate.getRelevantExperience()).thenReturn(3.0);
        when(candidate.getCurrentCtc()).thenReturn(10.0);
        when(candidate.getExpectedCtc()).thenReturn(15.0);
        when(candidate.getNoticePeriod()).thenReturn(30);
        when(candidate.getPreferredLocation()).thenReturn("Bangalore");
        when(candidate.getSource()).thenReturn("LinkedIn");
        when(candidate.getResumeUrl()).thenReturn("http://resume.url");

        when(jd.getId()).thenReturn(1L);
        when(jd.getTitle()).thenReturn("Java Dev");
        when(jd.getDescription()).thenReturn("Java backend");
        when(jd.getSkills()).thenReturn("Java, Spring");
        when(jd.getLocation()).thenReturn("Bangalore");
        when(jd.getJobType()).thenReturn(JobType.FULL_TIME);

        return app;
    }

    @Test
    void createPanel_returnsOk() {
        PanelRequest req = new PanelRequest();
        req.setFullName("Panel Member");
        req.setEmail("panel@example.com");

        ResponseEntity<?> result = controller.createPanel(req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(panelService).createPanel(req);
    }

    @Test
    void getAllCandidates_noFilter_returnsEmptyList() {
        when(applicationRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        ResponseEntity<?> result = controller.getAllCandidates(null, null, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAllCandidates_withStageFilter_filtersCorrectly() {
        Application app = buildMockApplication();
        when(applicationRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(app));

        ResponseEntity<?> result = controller.getAllCandidates("PROFILING", null, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAllCandidates_withStatusFilter_filtersCorrectly() {
        Application app = buildMockApplication();
        when(applicationRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(app));

        ResponseEntity<?> result = controller.getAllCandidates(null, "APPLIED", null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAllCandidates_withJobIdFilter_filtersCorrectly() {
        Application app = buildMockApplication();
        when(applicationRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(app));

        ResponseEntity<?> result = controller.getAllCandidates(null, null, 1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getCandidateDetail_notFound_throwsBadRequest() {
        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> controller.getCandidateDetail(99L));
    }

    @Test
    void getCandidateDetail_success_returnsResponse() {
        Application app = buildMockApplication();
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));
        when(interviewRepository.findByCandidate(any())).thenReturn(List.of());
        when(feedbackService.getFeedbackByInterview(anyLong())).thenReturn(List.of());

        ResponseEntity<?> result = controller.getCandidateDetail(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void scheduleInterview_withAuth_returnsCreated() {
        InterviewScheduleRequest req = new InterviewScheduleRequest();
        req.setApplicationId(1L);
        req.setRound(InterviewRound.L1);
        req.setInterviewDateTime(LocalDateTime.now());

        when(interviewService.scheduleInterview(any(), eq("hr@example.com")))
                .thenReturn(new InterviewResponse());

        ResponseEntity<?> result = controller.scheduleInterview(req, authentication);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void scheduleInterview_nullAuth_returnsCreated() {
        InterviewScheduleRequest req = new InterviewScheduleRequest();
        req.setApplicationId(1L);
        req.setRound(InterviewRound.L1);
        req.setInterviewDateTime(LocalDateTime.now());

        when(interviewService.scheduleInterview(any(), isNull()))
                .thenReturn(new InterviewResponse());

        ResponseEntity<?> result = controller.scheduleInterview(req, null);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void getHRInterviews_returnsOk() {
        when(interviewRepository.findByRoundOrderByInterviewDateTimeDesc(InterviewRound.HR))
                .thenReturn(List.of());

        ResponseEntity<?> result = controller.getHRInterviews();

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getCandidateForHRInterview_notFound_throwsBadRequest() {
        when(interviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> controller.getCandidateForHRInterview(99L));
    }

    @Test
    void getCandidateForHRInterview_notHRRound_throwsBadRequest() {
        Interview interview = mock(Interview.class);
        when(interview.getRound()).thenReturn(InterviewRound.L1);
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));

        assertThrows(BadRequestException.class, () -> controller.getCandidateForHRInterview(1L));
    }

    @Test
    void getCandidateForHRInterview_noApplication_throwsBadRequest() {
        Candidate candidate = mock(Candidate.class);
        Interview interview = mock(Interview.class);
        when(interview.getRound()).thenReturn(InterviewRound.HR);
        when(interview.getCandidate()).thenReturn(candidate);
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(applicationRepository.findByCandidateOrderByCreatedAtDesc(candidate)).thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> controller.getCandidateForHRInterview(1L));
    }

    @Test
    void getCandidateForHRInterview_success_returnsOk() {
        Application app = buildMockApplication();
        Candidate candidate = app.getCandidate();

        Interview interview = mock(Interview.class);
        when(interview.getRound()).thenReturn(InterviewRound.HR);
        when(interview.getCandidate()).thenReturn(candidate);
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(applicationRepository.findByCandidateOrderByCreatedAtDesc(candidate))
                .thenReturn(List.of(app));
        when(interviewRepository.findByCandidate(candidate)).thenReturn(List.of());

        ResponseEntity<?> result = controller.getCandidateForHRInterview(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void submitHRFeedback_returnsCreated() {
        FeedbackRequest req = new FeedbackRequest();
        req.setComments("Good candidate");
        req.setAreasCovered("Java, Spring");
        req.setRating(4);
        req.setStatus(FeedbackStatus.SELECTED);

        when(feedbackService.submitHRFeedback(eq(1L), eq("hr@example.com"), any()))
                .thenReturn(new FeedbackResponse());

        ResponseEntity<?> result = controller.submitHRFeedback(1L, req, authentication);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void getFeedbackForInterview_returnsOk() {
        when(feedbackService.getFeedbackByInterview(1L)).thenReturn(List.of());

        ResponseEntity<?> result = controller.getFeedbackForInterview(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void updateStage_success_profilingToScreening() {
        Application app = buildMockApplication();
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        StageUpdateRequest req = new StageUpdateRequest();
        req.setStage("SCREENING");

        ResponseEntity<?> result = controller.updateStage(1L, req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(app).setStage(InterviewStage.SCREENING);
        verify(applicationRepository).save(app);
    }

    @Test
    void updateStage_rejected_throwsBadRequest() {
        Application app = mock(Application.class);
        when(app.getStatus()).thenReturn(ApplicationStatus.REJECTED);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        StageUpdateRequest req = new StageUpdateRequest();
        req.setStage("SCREENING");

        assertThrows(BadRequestException.class, () -> controller.updateStage(1L, req));
    }

    @Test
    void updateStage_selected_throwsBadRequest() {
        Application app = mock(Application.class);
        when(app.getStatus()).thenReturn(ApplicationStatus.SELECTED);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        StageUpdateRequest req = new StageUpdateRequest();
        req.setStage("HR");

        assertThrows(BadRequestException.class, () -> controller.updateStage(1L, req));
    }

    @Test
    void updateStage_alreadyFinalStage_throwsBadRequest() {
        Application app = mock(Application.class);
        when(app.getStatus()).thenReturn(ApplicationStatus.APPLIED);
        when(app.getStage()).thenReturn(InterviewStage.HR);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        StageUpdateRequest req = new StageUpdateRequest();
        req.setStage("HR");

        assertThrows(BadRequestException.class, () -> controller.updateStage(1L, req));
    }

    @Test
    void updateStage_nonSequential_throwsBadRequest() {
        Application app = mock(Application.class);
        when(app.getStatus()).thenReturn(ApplicationStatus.APPLIED);
        when(app.getStage()).thenReturn(InterviewStage.PROFILING);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        StageUpdateRequest req = new StageUpdateRequest();
        req.setStage("L1");

        assertThrows(BadRequestException.class, () -> controller.updateStage(1L, req));
    }

    @Test
    void updateStage_notFound_throwsBadRequest() {
        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        StageUpdateRequest req = new StageUpdateRequest();
        req.setStage("SCREENING");

        assertThrows(BadRequestException.class, () -> controller.updateStage(99L, req));
    }

    @Test
    void rejectCandidate_success_returnsOk() {
        Application app = buildMockApplication();
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        ResponseEntity<?> result = controller.rejectCandidate(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(app).setStatus(ApplicationStatus.REJECTED);
    }

    @Test
    void rejectCandidate_notFound_throwsBadRequest() {
        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> controller.rejectCandidate(99L));
    }

    @Test
    void selectCandidate_success_returnsOk() {
        Application app = mock(Application.class);
        when(app.getStage()).thenReturn(InterviewStage.HR);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        ResponseEntity<?> result = controller.selectCandidate(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(app).setStatus(ApplicationStatus.SELECTED);
    }

    @Test
    void selectCandidate_notHRStage_throwsBadRequest() {
        Application app = mock(Application.class);
        when(app.getStage()).thenReturn(InterviewStage.L1);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        assertThrows(BadRequestException.class, () -> controller.selectCandidate(1L));
    }

    @Test
    void selectCandidate_notFound_throwsBadRequest() {
        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> controller.selectCandidate(99L));
    }

    @Test
    void getAllPanels_returnsOk() {
        Panel panel = mock(Panel.class);
        when(panel.getId()).thenReturn(1L);
        when(panel.getFullName()).thenReturn("Panel Member");
        when(panel.getEmail()).thenReturn("panel@example.com");
        when(panel.getPhone()).thenReturn("9876543210");
        when(panel.getOrganization()).thenReturn("TechCorp");
        when(panel.getDesignation()).thenReturn("Senior Engineer");
        when(panelRepository.findAll()).thenReturn(List.of(panel));

        ResponseEntity<?> result = controller.getAllPanels();

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
