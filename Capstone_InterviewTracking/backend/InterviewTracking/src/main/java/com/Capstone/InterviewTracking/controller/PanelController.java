package com.Capstone.InterviewTracking.controller;

import com.Capstone.InterviewTracking.constant.AppConstants;
import com.Capstone.InterviewTracking.dto.ApiResponse;
import com.Capstone.InterviewTracking.dto.CandidateDetailResponse;
import com.Capstone.InterviewTracking.dto.FeedbackRequest;
import com.Capstone.InterviewTracking.dto.FeedbackResponse;
import com.Capstone.InterviewTracking.dto.InterviewResponse;
import com.Capstone.InterviewTracking.dto.PanelResponse;
import com.Capstone.InterviewTracking.entity.Application;
import com.Capstone.InterviewTracking.entity.Candidate;
import com.Capstone.InterviewTracking.entity.Interview;
import com.Capstone.InterviewTracking.entity.JobDescription;
import com.Capstone.InterviewTracking.entity.Panel;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import com.Capstone.InterviewTracking.repository.ApplicationRepository;
import com.Capstone.InterviewTracking.repository.InterviewRepository;
import com.Capstone.InterviewTracking.repository.PanelRepository;
import com.Capstone.InterviewTracking.service.FeedbackService;
import com.Capstone.InterviewTracking.service.InterviewService;
import com.Capstone.InterviewTracking.service.impl.InterviewServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping(AppConstants.PANEL_INTERVIEWS_PATH)
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> getMyInterviews(Authentication authentication) {
        List<InterviewResponse> interviews = interviewService.getInterviewsByPanel(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(AppConstants.MSG_INTERVIEWS_FETCHED, interviews));
    }

    /**
     * Returns the candidate detail for a specific interview assigned to the panel member.
     *
     * @param interviewId the interview ID
     * @param authentication the authenticated panel user
     * @return the candidate detail response
     */
    @GetMapping(AppConstants.PANEL_INTERVIEW_CANDIDATE_PATH)
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

        CandidateDetailResponse candidateDetailResponse = buildCandidateResponse(interview, application);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.MSG_CANDIDATE_DETAIL_FETCHED, candidateDetailResponse));
    }

    /**
     * Submits feedback for an interview from the authenticated panel member.
     *
     * @param interviewId the interview ID
     * @param request the feedback details
     * @param authentication the authenticated panel user
     * @return the saved feedback response
     */
    @PostMapping(AppConstants.PANEL_INTERVIEW_FEEDBACK_PATH)
    public ResponseEntity<ApiResponse<FeedbackResponse>> submitFeedback(
            @PathVariable Long interviewId,
            @RequestBody @Valid FeedbackRequest request,
            Authentication authentication) {

        FeedbackResponse feedbackResponse = feedbackService.submitFeedback(
                interviewId, authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(AppConstants.MSG_FEEDBACK_SUBMITTED, feedbackResponse));
    }

    /**
     * Builds a CandidateDetailResponse from an interview and its associated application.
     *
     * @param interview the interview entity
     * @param application the candidate's application
     * @return the candidate detail response
     */
    private CandidateDetailResponse buildCandidateResponse(Interview interview, Application application) {
        Candidate candidate = interview.getCandidate();
        JobDescription jobDescription = application.getJob();

        CandidateDetailResponse candidateDetailResponse = new CandidateDetailResponse();
        candidateDetailResponse.setApplicationId(application.getId());
        candidateDetailResponse.setCandidateId(candidate.getId());
        candidateDetailResponse.setFullName(candidate.getFullName());
        candidateDetailResponse.setEmail(candidate.getEmail());
        candidateDetailResponse.setMobile(candidate.getMobile());
        candidateDetailResponse.setCurrentCompany(candidate.getCurrentCompany());
        candidateDetailResponse.setTotalExperience(candidate.getTotalExperience());
        candidateDetailResponse.setRelevantExperience(candidate.getRelevantExperience());
        candidateDetailResponse.setCurrentCtc(candidate.getCurrentCtc());
        candidateDetailResponse.setExpectedCtc(candidate.getExpectedCtc());
        candidateDetailResponse.setNoticePeriod(candidate.getNoticePeriod());
        candidateDetailResponse.setPreferredLocation(candidate.getPreferredLocation());
        candidateDetailResponse.setSource(candidate.getSource());
        candidateDetailResponse.setResumeUrl(candidate.getResumeUrl());
        candidateDetailResponse.setJobId(jobDescription.getId());
        candidateDetailResponse.setJobTitle(jobDescription.getTitle());
        candidateDetailResponse.setJobDescription(jobDescription.getDescription());
        candidateDetailResponse.setSkills(jobDescription.getSkills());
        candidateDetailResponse.setLocation(jobDescription.getLocation());
        candidateDetailResponse.setJobType(jobDescription.getJobType() != null ? jobDescription.getJobType().name() : null);
        candidateDetailResponse.setStage(application.getStage().name());
        candidateDetailResponse.setStatus(application.getStatus().name());
        candidateDetailResponse.setAppliedAt(application.getCreatedAt());
        List<Interview> allInterviews = interviewRepository.findByCandidate(candidate);
        candidateDetailResponse.setInterviews(allInterviews.stream()
                .map(interviewServiceImpl::toResponse)
                .collect(Collectors.toList()));
        return candidateDetailResponse;
    }
}
