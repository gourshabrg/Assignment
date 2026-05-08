package com.Capstone.InterviewTracking.controller;

import com.Capstone.InterviewTracking.constant.AppConstants;
import com.Capstone.InterviewTracking.dto.*;
import com.Capstone.InterviewTracking.entity.*;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import com.Capstone.InterviewTracking.repository.*;
import com.Capstone.InterviewTracking.service.FeedbackService;
import com.Capstone.InterviewTracking.service.InterviewService;
import com.Capstone.InterviewTracking.service.impl.InterviewServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for panel member operations such as viewing interviews and submitting feedback.
 */
@RestController
@RequestMapping(AppConstants.PANEL_BASE_PATH)
public class PanelController {

    private final InterviewService interviewService;
    private final InterviewServiceImpl interviewServiceImpl;
    private final FeedbackService feedbackService;
    private final InterviewRepository interviewRepository;
    private final PanelRepository panelRepository;
    private final ApplicationRepository applicationRepository;

    /**
     * Creates a PanelController with all required dependencies.
     *
     * @param interviewService the interview service
     * @param interviewServiceImpl the interview service implementation
     * @param feedbackService the feedback service
     * @param interviewRepository the interview repository
     * @param panelRepository the panel repository
     * @param applicationRepository the application repository
     */
    public PanelController(InterviewService interviewService,
                           InterviewServiceImpl interviewServiceImpl,
                           FeedbackService feedbackService,
                           InterviewRepository interviewRepository,
                           PanelRepository panelRepository,
                           ApplicationRepository applicationRepository) {
        this.interviewService = interviewService;
        this.interviewServiceImpl = interviewServiceImpl;
        this.feedbackService = feedbackService;
        this.interviewRepository = interviewRepository;
        this.panelRepository = panelRepository;
        this.applicationRepository = applicationRepository;
    }

    /**
     * Returns all interviews assigned to the authenticated panel member.
     *
     * @param authentication the authenticated panel user
     * @return the list of interview responses
     */
    @GetMapping("/interviews")
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> getMyInterviews(Authentication authentication) {
        List<InterviewResponse> interviews = interviewService.getInterviewsByPanel(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Interviews fetched", interviews));
    }

    /**
     * Returns the candidate detail for a specific interview assigned to the panel member.
     *
     * @param interviewId the interview ID
     * @param authentication the authenticated panel user
     * @return the candidate detail response
     */
    @GetMapping("/interviews/{interviewId}/candidate")
    public ResponseEntity<ApiResponse<CandidateDetailResponse>> getCandidateForInterview(
            @PathVariable Long interviewId,
            Authentication authentication) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new BadRequestException("Interview not found"));

        Panel panel = panelRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BadRequestException("Panel member not found"));

        boolean isAssigned = interview.getPanels().stream()
                .anyMatch(p -> p.getId().equals(panel.getId()));
        if (!isAssigned) {
            throw new BadRequestException("You are not assigned to this interview");
        }

        Candidate candidate = interview.getCandidate();
        List<Application> apps = applicationRepository.findByCandidateOrderByCreatedAtDesc(candidate);
        if (apps.isEmpty()) throw new BadRequestException("Application not found");
        Application application = apps.get(0);

        CandidateDetailResponse response = buildCandidateResponse(interview, application);
        return ResponseEntity.ok(ApiResponse.success("Candidate detail fetched", response));
    }

    /**
     * Submits feedback for an interview from the authenticated panel member.
     *
     * @param interviewId the interview ID
     * @param request the feedback details
     * @param authentication the authenticated panel user
     * @return the saved feedback response
     */
    @PostMapping("/interviews/{interviewId}/feedback")
    public ResponseEntity<ApiResponse<FeedbackResponse>> submitFeedback(
            @PathVariable Long interviewId,
            @RequestBody @Valid FeedbackRequest request,
            Authentication authentication) {

        FeedbackResponse response = feedbackService.submitFeedback(
                interviewId, authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Feedback submitted successfully", response));
    }

    /**
     * Builds a CandidateDetailResponse from an interview and its associated application.
     *
     * @param interview the interview entity
     * @param application the candidate's application
     * @return the candidate detail response
     */
    private CandidateDetailResponse buildCandidateResponse(Interview interview, Application application) {
        Candidate c = interview.getCandidate();
        JobDescription jd = application.getJob();

        CandidateDetailResponse r = new CandidateDetailResponse();
        r.setApplicationId(application.getId());
        r.setCandidateId(c.getId());
        r.setFullName(c.getFullName());
        r.setEmail(c.getEmail());
        r.setMobile(c.getMobile());
        r.setCurrentCompany(c.getCurrentCompany());
        r.setTotalExperience(c.getTotalExperience());
        r.setRelevantExperience(c.getRelevantExperience());
        r.setCurrentCtc(c.getCurrentCtc());
        r.setExpectedCtc(c.getExpectedCtc());
        r.setNoticePeriod(c.getNoticePeriod());
        r.setPreferredLocation(c.getPreferredLocation());
        r.setSource(c.getSource());
        r.setResumeUrl(c.getResumeUrl());
        r.setJobId(jd.getId());
        r.setJobTitle(jd.getTitle());
        r.setJobDescription(jd.getDescription());
        r.setSkills(jd.getSkills());
        r.setLocation(jd.getLocation());
        r.setJobType(jd.getJobType() != null ? jd.getJobType().name() : null);
        r.setStage(application.getStage().name());
        r.setStatus(application.getStatus().name());
        r.setAppliedAt(application.getCreatedAt());
        List<Interview> allInterviews = interviewRepository.findByCandidate(c);
        r.setInterviews(allInterviews.stream()
                .map(interviewServiceImpl::toResponse)
                .collect(Collectors.toList()));
        return r;
    }
}
