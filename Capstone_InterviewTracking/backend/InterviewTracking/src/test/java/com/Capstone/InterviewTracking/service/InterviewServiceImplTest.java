package com.Capstone.InterviewTracking.service;

import com.Capstone.InterviewTracking.dto.InterviewResponse;
import com.Capstone.InterviewTracking.dto.InterviewScheduleRequest;
import com.Capstone.InterviewTracking.entity.Application;
import com.Capstone.InterviewTracking.entity.Candidate;
import com.Capstone.InterviewTracking.entity.Interview;
import com.Capstone.InterviewTracking.entity.Panel;
import com.Capstone.InterviewTracking.enums.ApplicationStatus;
import com.Capstone.InterviewTracking.enums.InterviewRound;
import com.Capstone.InterviewTracking.enums.InterviewStage;
import com.Capstone.InterviewTracking.enums.InterviewStatus;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import com.Capstone.InterviewTracking.repository.ApplicationRepository;
import com.Capstone.InterviewTracking.repository.InterviewRepository;
import com.Capstone.InterviewTracking.repository.PanelRepository;
import com.Capstone.InterviewTracking.service.impl.InterviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceImplTest {

    @Mock private ApplicationRepository applicationRepository;
    @Mock private PanelRepository panelRepository;
    @Mock private InterviewRepository interviewRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private InterviewServiceImpl service;

    private Candidate buildCandidate() {
        Candidate c = new Candidate();
        c.setId(1L);
        c.setEmail("candidate@example.com");
        c.setFullName("John Doe");
        return c;
    }

    private Application buildApplication(InterviewStage stage) {
        Application a = new Application();
        ReflectionTestUtils.setField(a, "id", 10L);
        a.setCandidate(buildCandidate());
        a.setStage(stage);
        a.setStatus(ApplicationStatus.APPLIED);
        return a;
    }

    private Panel buildPanel(Long id, String name) {
        Panel p = new Panel();
        p.setId(id);
        p.setFullName(name);
        p.setEmail(name.toLowerCase().replace(" ", "") + "@example.com");
        return p;
    }

    private Interview buildSavedInterview(InterviewRound round, List<Panel> panels) {
        Interview i = new Interview();
        i.setId(100L);
        i.setCandidate(buildCandidate());
        i.setRound(round);
        i.setInterviewDateTime(LocalDateTime.now().plusDays(1));
        i.setStatus(InterviewStatus.SCHEDULED);
        i.setPanels(panels);
        return i;
    }

    // ── scheduleInterview ─────────────────────────────────────────────────────────

    @Test
    void scheduleInterview_validL1Round_returnsInterviewResponse() {
        Application app = buildApplication(InterviewStage.SCREENING);
        Panel panel = buildPanel(1L, "Alice");
        InterviewScheduleRequest req = new InterviewScheduleRequest();
        req.setApplicationId(10L);
        req.setRound(InterviewRound.L1);
        req.setInterviewDateTime(LocalDateTime.now().plusDays(1));
        req.setPanelIds(List.of(1L));

        Interview saved = buildSavedInterview(InterviewRound.L1, List.of(panel));

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(app));
        when(interviewRepository.existsByCandidateAndRound(app.getCandidate(), InterviewRound.L1)).thenReturn(false);
        when(panelRepository.findAllById(List.of(1L))).thenReturn(List.of(panel));
        when(interviewRepository.save(any(Interview.class))).thenReturn(saved);

        InterviewResponse result = service.scheduleInterview(req, "hr@example.com");

        assertNotNull(result);
        assertEquals("L1", result.getRound());
        verify(emailService).sendInterviewScheduledMail(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void scheduleInterview_validHrRound_sendsHrEmail() {
        Application app = buildApplication(InterviewStage.L2);
        InterviewScheduleRequest req = new InterviewScheduleRequest();
        req.setApplicationId(10L);
        req.setRound(InterviewRound.HR);
        req.setInterviewDateTime(LocalDateTime.now().plusDays(2));
        req.setPanelIds(null);

        Interview saved = buildSavedInterview(InterviewRound.HR, List.of());

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(app));
        when(interviewRepository.existsByCandidateAndRound(app.getCandidate(), InterviewRound.HR)).thenReturn(false);
        when(interviewRepository.save(any(Interview.class))).thenReturn(saved);

        InterviewResponse result = service.scheduleInterview(req, "hr@example.com");

        assertNotNull(result);
        verify(emailService).sendHRInterviewScheduledMail(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void scheduleInterview_applicationNotFound_throwsBadRequestException() {
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        InterviewScheduleRequest req = new InterviewScheduleRequest();
        req.setApplicationId(999L);

        assertThrows(BadRequestException.class, () -> service.scheduleInterview(req, "hr@example.com"));
    }

    @Test
    void scheduleInterview_candidateAtFinalStage_throwsBadRequestException() {
        Application app = buildApplication(InterviewStage.HR);

        InterviewScheduleRequest req = new InterviewScheduleRequest();
        req.setApplicationId(10L);
        req.setRound(InterviewRound.L1);

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(app));

        assertThrows(BadRequestException.class, () -> service.scheduleInterview(req, "hr@example.com"));
    }

    @Test
    void scheduleInterview_wrongRoundForStage_throwsBadRequestException() {
        Application app = buildApplication(InterviewStage.PROFILING);

        InterviewScheduleRequest req = new InterviewScheduleRequest();
        req.setApplicationId(10L);
        req.setRound(InterviewRound.L1);

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(app));

        assertThrows(BadRequestException.class, () -> service.scheduleInterview(req, "hr@example.com"));
    }

    @Test
    void scheduleInterview_roundAlreadyScheduled_throwsBadRequestException() {
        Application app = buildApplication(InterviewStage.PROFILING);

        InterviewScheduleRequest req = new InterviewScheduleRequest();
        req.setApplicationId(10L);
        req.setRound(InterviewRound.SCREENING);

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(app));
        when(interviewRepository.existsByCandidateAndRound(app.getCandidate(), InterviewRound.SCREENING)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.scheduleInterview(req, "hr@example.com"));
    }

    @Test
    void scheduleInterview_noPanelIdsForNonHrRound_throwsBadRequestException() {
        Application app = buildApplication(InterviewStage.PROFILING);

        InterviewScheduleRequest req = new InterviewScheduleRequest();
        req.setApplicationId(10L);
        req.setRound(InterviewRound.SCREENING);
        req.setPanelIds(List.of());

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(app));
        when(interviewRepository.existsByCandidateAndRound(app.getCandidate(), InterviewRound.SCREENING)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> service.scheduleInterview(req, "hr@example.com"));
    }

    @Test
    void scheduleInterview_moreThanTwoPanels_throwsBadRequestException() {
        Application app = buildApplication(InterviewStage.PROFILING);

        InterviewScheduleRequest req = new InterviewScheduleRequest();
        req.setApplicationId(10L);
        req.setRound(InterviewRound.SCREENING);
        req.setPanelIds(List.of(1L, 2L, 3L));

        when(applicationRepository.findById(10L)).thenReturn(Optional.of(app));
        when(interviewRepository.existsByCandidateAndRound(app.getCandidate(), InterviewRound.SCREENING)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> service.scheduleInterview(req, "hr@example.com"));
    }

    // ── getInterviewsByPanel ──────────────────────────────────────────────────────

    @Test
    void getInterviewsByPanel_validEmail_returnsInterviewList() {
        Panel panel = buildPanel(1L, "Alice");
        Interview interview = buildSavedInterview(InterviewRound.L1, List.of(panel));

        when(panelRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(panel));
        when(interviewRepository.findByPanelsContaining(panel)).thenReturn(List.of(interview));

        List<InterviewResponse> result = service.getInterviewsByPanel("alice@example.com");

        assertEquals(1, result.size());
    }

    @Test
    void getInterviewsByPanel_panelNotFound_throwsBadRequestException() {
        when(panelRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> service.getInterviewsByPanel("unknown@example.com"));
    }

    // ── getInterviewsByCandidate ──────────────────────────────────────────────────

    @Test
    void getInterviewsByCandidate_returnsFilteredList() {
        Interview interview = buildSavedInterview(InterviewRound.L1, List.of());

        when(interviewRepository.findAll()).thenReturn(List.of(interview));

        List<InterviewResponse> result = service.getInterviewsByCandidate(1L);

        assertEquals(1, result.size());
    }

    // ── toResponse ────────────────────────────────────────────────────────────────

    @Test
    void toResponse_withApplication_setsApplicationId() {
        Application app = buildApplication(InterviewStage.L1);

        Interview interview = buildSavedInterview(InterviewRound.L1, List.of());

        InterviewResponse result = service.toResponse(interview, app);

        assertEquals(10L, result.getApplicationId());
        assertEquals("L1", result.getRound());
        assertEquals("John Doe", result.getCandidateName());
    }

    @Test
    void toResponse_withoutApplication_lookupsFromRepo() {
        Application app = buildApplication(InterviewStage.L1);

        Interview interview = buildSavedInterview(InterviewRound.L2, List.of());

        when(applicationRepository.findByCandidateOrderByCreatedAtDesc(any(Candidate.class)))
                .thenReturn(List.of(app));

        InterviewResponse result = service.toResponse(interview);

        assertEquals(10L, result.getApplicationId());
    }
}
