package com.Capstone.InterviewTracking.service;

import com.Capstone.InterviewTracking.dto.FeedbackRequest;
import com.Capstone.InterviewTracking.dto.FeedbackResponse;
import com.Capstone.InterviewTracking.entity.Feedback;
import com.Capstone.InterviewTracking.entity.Interview;
import com.Capstone.InterviewTracking.entity.Panel;
import com.Capstone.InterviewTracking.enums.FeedbackStatus;
import com.Capstone.InterviewTracking.enums.InterviewRound;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import com.Capstone.InterviewTracking.repository.FeedbackRepository;
import com.Capstone.InterviewTracking.repository.InterviewRepository;
import com.Capstone.InterviewTracking.repository.PanelRepository;
import com.Capstone.InterviewTracking.service.impl.FeedbackServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

    @Mock private FeedbackRepository feedbackRepository;
    @Mock private InterviewRepository interviewRepository;
    @Mock private PanelRepository panelRepository;

    @InjectMocks
    private FeedbackServiceImpl service;

    private Panel buildPanel(Long id, String email) {
        Panel p = new Panel();
        p.setId(id);
        p.setEmail(email);
        p.setFullName("Panel Member");
        return p;
    }

    private Interview buildInterview(InterviewRound round, List<Panel> panels) {
        Interview i = new Interview();
        i.setId(1L);
        i.setRound(round);
        i.setPanels(panels);
        return i;
    }

    private FeedbackRequest buildRequest() {
        FeedbackRequest r = new FeedbackRequest();
        r.setComments("Good candidate");
        r.setStrengths("Technical skills");
        r.setWeaknesses("Communication");
        r.setAreasCovered("Java, Spring");
        r.setRating(4);
        r.setStatus(FeedbackStatus.SELECTED);
        return r;
    }

    // ── submitFeedback ───────────────────────────────────────────────────────────

    @Test
    void submitFeedback_validRequest_returnsSavedFeedback() {
        Panel panel = buildPanel(1L, "panel@example.com");
        Interview interview = buildInterview(InterviewRound.L1, List.of(panel));
        Feedback saved = new Feedback();
        saved.setId(10L);
        saved.setInterview(interview);
        saved.setPanel(panel);
        saved.setStatus(FeedbackStatus.SELECTED);
        saved.setComments("Good");

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(panelRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(panel));
        when(feedbackRepository.existsByInterviewAndPanel(interview, panel)).thenReturn(false);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(saved);

        FeedbackResponse result = service.submitFeedback(1L, "panel@example.com", buildRequest());

        assertNotNull(result);
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void submitFeedback_interviewNotFound_throwsBadRequestException() {
        when(interviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> service.submitFeedback(99L, "panel@example.com", buildRequest()));
    }

    @Test
    void submitFeedback_panelNotFound_throwsBadRequestException() {
        Interview interview = buildInterview(InterviewRound.L1, List.of());

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(panelRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> service.submitFeedback(1L, "unknown@example.com", buildRequest()));
    }

    @Test
    void submitFeedback_panelNotAssigned_throwsBadRequestException() {
        Panel assignedPanel = buildPanel(1L, "assigned@example.com");
        Panel requestingPanel = buildPanel(2L, "other@example.com");
        Interview interview = buildInterview(InterviewRound.L1, List.of(assignedPanel));

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(panelRepository.findByEmail("other@example.com")).thenReturn(Optional.of(requestingPanel));

        assertThrows(BadRequestException.class,
                () -> service.submitFeedback(1L, "other@example.com", buildRequest()));
    }

    @Test
    void submitFeedback_alreadySubmitted_throwsBadRequestException() {
        Panel panel = buildPanel(1L, "panel@example.com");
        Interview interview = buildInterview(InterviewRound.L1, List.of(panel));

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(panelRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(panel));
        when(feedbackRepository.existsByInterviewAndPanel(interview, panel)).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> service.submitFeedback(1L, "panel@example.com", buildRequest()));
    }

    // ── submitHRFeedback ─────────────────────────────────────────────────────────

    @Test
    void submitHRFeedback_validHrRound_returnsSavedFeedback() {
        Interview interview = buildInterview(InterviewRound.HR, List.of());
        Feedback saved = new Feedback();
        saved.setId(20L);
        saved.setInterview(interview);
        saved.setHrReviewer("hr@example.com");
        saved.setStatus(FeedbackStatus.SELECTED);
        saved.setComments("Final round passed");

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(feedbackRepository.existsByInterviewAndHrReviewer(interview, "hr@example.com")).thenReturn(false);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(saved);

        FeedbackResponse result = service.submitHRFeedback(1L, "hr@example.com", buildRequest());

        assertNotNull(result);
    }

    @Test
    void submitHRFeedback_interviewNotFound_throwsBadRequestException() {
        when(interviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> service.submitHRFeedback(99L, "hr@example.com", buildRequest()));
    }

    @Test
    void submitHRFeedback_notHrRound_throwsBadRequestException() {
        Interview interview = buildInterview(InterviewRound.L1, List.of());
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));

        assertThrows(BadRequestException.class,
                () -> service.submitHRFeedback(1L, "hr@example.com", buildRequest()));
    }

    @Test
    void submitHRFeedback_alreadySubmitted_throwsBadRequestException() {
        Interview interview = buildInterview(InterviewRound.HR, List.of());

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(feedbackRepository.existsByInterviewAndHrReviewer(interview, "hr@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> service.submitHRFeedback(1L, "hr@example.com", buildRequest()));
    }

    // ── getFeedbackByInterview ────────────────────────────────────────────────────

    @Test
    void getFeedbackByInterview_returnsListOfResponses() {
        Interview interview = buildInterview(InterviewRound.L1, List.of());
        Panel panel = buildPanel(1L, "panel@example.com");

        Feedback feedback = new Feedback();
        feedback.setId(1L);
        feedback.setInterview(interview);
        feedback.setPanel(panel);
        feedback.setStatus(FeedbackStatus.SELECTED);
        feedback.setComments("Good");

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(feedbackRepository.findByInterview(interview)).thenReturn(List.of(feedback));

        List<FeedbackResponse> result = service.getFeedbackByInterview(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getFeedbackByInterview_interviewNotFound_throwsBadRequestException() {
        when(interviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> service.getFeedbackByInterview(99L));
    }

    // ── toResponse ───────────────────────────────────────────────────────────────

    @Test
    void toResponse_withPanel_setsPanelNameFromPanel() {
        Panel panel = buildPanel(1L, "panel@example.com");
        Interview interview = buildInterview(InterviewRound.L1, List.of(panel));

        Feedback feedback = new Feedback();
        feedback.setId(1L);
        feedback.setInterview(interview);
        feedback.setPanel(panel);
        feedback.setStatus(FeedbackStatus.SELECTED);
        feedback.setComments("Great");
        feedback.setRating(5);

        FeedbackResponse result = service.toResponse(feedback);

        assertEquals("Panel Member", result.getPanelName());
        assertEquals("L1", result.getRound());
        assertEquals("SELECTED", result.getStatus());
    }

    @Test
    void toResponse_withNullPanelAndHrReviewer_setsPanelNameFromHrReviewer() {
        Interview interview = buildInterview(InterviewRound.HR, List.of());

        Feedback feedback = new Feedback();
        feedback.setId(2L);
        feedback.setInterview(interview);
        feedback.setPanel(null);
        feedback.setHrReviewer("hr@example.com");
        feedback.setStatus(FeedbackStatus.SELECTED);

        FeedbackResponse result = service.toResponse(feedback);

        assertEquals("hr@example.com", result.getPanelName());
    }

    @Test
    void toResponse_withNullPanelAndNullHrReviewer_setsPanelNameToHR() {
        Interview interview = buildInterview(InterviewRound.HR, List.of());

        Feedback feedback = new Feedback();
        feedback.setId(3L);
        feedback.setInterview(interview);
        feedback.setPanel(null);
        feedback.setHrReviewer(null);
        feedback.setStatus(FeedbackStatus.REJECTED);

        FeedbackResponse result = service.toResponse(feedback);

        assertEquals("HR", result.getPanelName());
    }
}
