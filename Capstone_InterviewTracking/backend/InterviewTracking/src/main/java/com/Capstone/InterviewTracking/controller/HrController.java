package com.Capstone.InterviewTracking.controller;

import com.Capstone.InterviewTracking.constant.AppConstants;
import com.Capstone.InterviewTracking.dto.ApiResponse;
import com.Capstone.InterviewTracking.dto.CandidateDetailResponse;
import com.Capstone.InterviewTracking.dto.CandidateListResponse;
import com.Capstone.InterviewTracking.dto.FeedbackRequest;
import com.Capstone.InterviewTracking.dto.FeedbackResponse;
import com.Capstone.InterviewTracking.dto.InterviewResponse;
import com.Capstone.InterviewTracking.dto.InterviewScheduleRequest;
import com.Capstone.InterviewTracking.dto.PanelRequest;
import com.Capstone.InterviewTracking.dto.PanelResponse;
import com.Capstone.InterviewTracking.dto.StageUpdateRequest;
import com.Capstone.InterviewTracking.entity.Application;
import com.Capstone.InterviewTracking.entity.Candidate;
import com.Capstone.InterviewTracking.entity.Interview;
import com.Capstone.InterviewTracking.entity.JobDescription;
import com.Capstone.InterviewTracking.enums.ApplicationStatus;
import com.Capstone.InterviewTracking.enums.InterviewRound;
import com.Capstone.InterviewTracking.enums.InterviewStage;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import com.Capstone.InterviewTracking.repository.ApplicationRepository;
import com.Capstone.InterviewTracking.repository.InterviewRepository;
import com.Capstone.InterviewTracking.repository.PanelRepository;
import com.Capstone.InterviewTracking.service.FeedbackService;
import com.Capstone.InterviewTracking.service.InterviewService;
import com.Capstone.InterviewTracking.service.PanelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for all HR-only operations such as managing candidates, interviews, and panels.
 */
@RestController
@RequestMapping(AppConstants.HR_BASE_PATH)
public class HrController {

    private final PanelService panelService;
    private final ApplicationRepository applicationRepository;
    private final PanelRepository panelRepository;
    private final InterviewService interviewService;
    private final FeedbackService feedbackService;
    private final InterviewRepository interviewRepository;

    /**
     * Creates an HrController with all required dependencies.
     *
     * @param panelService the panel service
     * @param applicationRepository the application repository
     * @param panelRepository the panel repository
     * @param interviewService the interview service
     * @param feedbackService the feedback service
     * @param interviewRepository the interview repository
     */
    public HrController(PanelService panelService,
                        ApplicationRepository applicationRepository,
                        PanelRepository panelRepository,
                        InterviewService interviewService,
                        FeedbackService feedbackService,
                        InterviewRepository interviewRepository) {
        this.panelService = panelService;
        this.applicationRepository = applicationRepository;
        this.panelRepository = panelRepository;
        this.interviewService = interviewService;
        this.feedbackService = feedbackService;
        this.interviewRepository = interviewRepository;
    }

    /**
     * Creates a new panel member account and sends a verification email.
     *
     * @param request the panel member's details
     * @return the creation response
     */
    @PostMapping("/create-panel")
    public ResponseEntity<ApiResponse<String>> createPanel(@RequestBody @Valid PanelRequest request) {
        panelService.createPanel(request);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.MSG_PANEL_CREATED, null));
    }

    /**
     * Returns a filtered list of all candidate applications.
     *
     * @param stage optional hiring stage filter
     * @param status optional application status filter
     * @param jobId optional job ID filter
     * @return the list of candidate applications
     */
    @GetMapping("/candidates")
    public ResponseEntity<ApiResponse<List<CandidateListResponse>>> getAllCandidates(
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long jobId) {

        List<Application> applications = applicationRepository.findAllByOrderByCreatedAtDesc();

        if (stage != null && !stage.isBlank()) {
            InterviewStage stageEnum = InterviewStage.valueOf(stage.toUpperCase());
            applications = applications.stream()
                    .filter(a -> a.getStage() == stageEnum)
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isBlank()) {
            ApplicationStatus statusEnum = ApplicationStatus.valueOf(status.toUpperCase());
            applications = applications.stream()
                    .filter(a -> a.getStatus() == statusEnum)
                    .collect(Collectors.toList());
        }

        if (jobId != null) {
            applications = applications.stream()
                    .filter(a -> a.getJob().getId().equals(jobId))
                    .collect(Collectors.toList());
        }

        List<CandidateListResponse> candidateList = applications.stream()
                .map(this::toCandidateListResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(AppConstants.MSG_CANDIDATES_FETCHED, candidateList));
    }

    /**
     * Returns the full profile and interview history for a candidate application.
     *
     * @param applicationId the application ID
     * @return the candidate detail response
     */
    @GetMapping("/candidates/{applicationId}")
    public ResponseEntity<ApiResponse<CandidateDetailResponse>> getCandidateDetail(
            @PathVariable Long applicationId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BadRequestException("Application not found"));

        CandidateDetailResponse candidateDetailResponse = buildDetailResponse(application, true);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.MSG_CANDIDATE_DETAIL_FETCHED, candidateDetailResponse));
    }

    /**
     * Schedules an interview for a candidate's current hiring stage.
     *
     * @param request the scheduling details
     * @param authentication the authenticated HR user
     * @return the scheduled interview response
     */
    @PostMapping("/interviews/schedule")
    public ResponseEntity<ApiResponse<InterviewResponse>> scheduleInterview(
            @RequestBody @Valid InterviewScheduleRequest request,
            Authentication authentication) {

        String hrEmail = authentication != null ? authentication.getName() : null;
        InterviewResponse interviewResponse = interviewService.scheduleInterview(request, hrEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(AppConstants.MSG_INTERVIEW_SCHEDULED, interviewResponse));
    }

    /**
     * Returns all HR-round interviews.
     *
     * @return the list of HR interview responses
     */
    @GetMapping("/interviews")
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> getHRInterviews() {
        List<Interview> hrInterviews = interviewRepository
                .findByRoundOrderByInterviewDateTimeDesc(InterviewRound.HR);

        List<InterviewResponse> hrInterviewResponses = hrInterviews.stream()
                .map(i -> interviewService.toResponse(i, null))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(AppConstants.MSG_HR_INTERVIEWS_FETCHED, hrInterviewResponses));
    }

    /**
     * Returns the candidate detail for a specific HR-round interview.
     *
     * @param interviewId the interview ID
     * @return the candidate detail response
     */
    @GetMapping("/interviews/{interviewId}/candidate")
    public ResponseEntity<ApiResponse<CandidateDetailResponse>> getCandidateForHRInterview(
            @PathVariable Long interviewId) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new BadRequestException("Interview not found"));

        if (interview.getRound() != InterviewRound.HR) {
            throw new BadRequestException("This endpoint is only for HR round interviews");
        }

        Candidate candidate = interview.getCandidate();
        List<Application> apps = applicationRepository.findByCandidateOrderByCreatedAtDesc(candidate);
        if (apps.isEmpty()) throw new BadRequestException("Application not found");

        CandidateDetailResponse candidateDetailResponse = buildDetailResponse(apps.get(0), false);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.MSG_CANDIDATE_DETAIL_FETCHED, candidateDetailResponse));
    }

    /**
     * Submits HR feedback for a completed HR-round interview.
     *
     * @param interviewId the interview ID
     * @param request the feedback details
     * @param authentication the authenticated HR user
     * @return the saved feedback response
     */
    @PostMapping("/interviews/{interviewId}/feedback")
    public ResponseEntity<ApiResponse<FeedbackResponse>> submitHRFeedback(
            @PathVariable Long interviewId,
            @RequestBody @Valid FeedbackRequest request,
            Authentication authentication) {

        FeedbackResponse feedbackResponse = feedbackService.submitHRFeedback(
                interviewId, authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(AppConstants.MSG_HR_FEEDBACK_SUBMITTED, feedbackResponse));
    }

    /**
     * Returns all feedback records for a given interview.
     *
     * @param interviewId the interview ID
     * @return the list of feedback responses
     */
    @GetMapping("/interviews/{interviewId}/feedback")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedbackForInterview(
            @PathVariable Long interviewId) {

        List<FeedbackResponse> feedbacks = feedbackService.getFeedbackByInterview(interviewId);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.MSG_FEEDBACK_FETCHED, feedbacks));
    }

    /**
     * Advances a candidate's application to the next hiring stage.
     *
     * @param applicationId the application ID
     * @param request the target stage
     * @return the stage update response
     */
    @PutMapping("/applications/{applicationId}/stage")
    public ResponseEntity<ApiResponse<String>> updateStage(
            @PathVariable Long applicationId,
            @RequestBody StageUpdateRequest request) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BadRequestException("Application not found"));

        if (application.getStatus() == ApplicationStatus.REJECTED) {
            throw new BadRequestException("Cannot update stage of a rejected application");
        }

        if (application.getStatus() == ApplicationStatus.SELECTED) {
            throw new BadRequestException("Cannot update stage of a selected candidate");
        }

        List<InterviewStage> stageOrder = Arrays.asList(
                InterviewStage.PROFILING, InterviewStage.SCREENING,
                InterviewStage.L1, InterviewStage.L2, InterviewStage.HR
        );

        InterviewStage currentStage = application.getStage();
        InterviewStage newStage = InterviewStage.valueOf(request.getStage().toUpperCase());

        int currentIdx = stageOrder.indexOf(currentStage);
        int newIdx = stageOrder.indexOf(newStage);

        if (currentIdx == stageOrder.size() - 1) {
            throw new BadRequestException("Candidate is already at the final stage: " + currentStage.name());
        }

        if (newIdx != currentIdx + 1) {
            throw new BadRequestException(
                    "Stage must progress sequentially. Current: " + currentStage.name()
                    + ". Next allowed stage: " + stageOrder.get(currentIdx + 1).name()
            );
        }

        application.setStage(newStage);
        applicationRepository.save(application);
        return ResponseEntity.ok(ApiResponse.success("Stage updated to " + newStage.name(), null));
    }

    /**
     * Marks a candidate's application as rejected.
     *
     * @param applicationId the application ID
     * @return the rejection response
     */
    @PutMapping("/applications/{applicationId}/reject")
    public ResponseEntity<ApiResponse<String>> rejectCandidate(
            @PathVariable Long applicationId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BadRequestException("Application not found"));

        application.setStatus(ApplicationStatus.REJECTED);
        applicationRepository.save(application);

        return ResponseEntity.ok(ApiResponse.success(AppConstants.MSG_CANDIDATE_REJECTED, null));
    }

    /**
     * Marks a candidate's application as selected.
     *
     * @param applicationId the application ID
     * @return the selection response
     */
    @PutMapping("/applications/{applicationId}/select")
    public ResponseEntity<ApiResponse<String>> selectCandidate(
            @PathVariable Long applicationId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BadRequestException("Application not found"));

        if (application.getStage() != InterviewStage.HR) {
            throw new BadRequestException("Final selection can only be done at HR stage");
        }

        application.setStatus(ApplicationStatus.SELECTED);
        applicationRepository.save(application);

        return ResponseEntity.ok(ApiResponse.success(AppConstants.MSG_CANDIDATE_SELECTED, null));
    }

    /**
     * Returns all registered panel members.
     *
     * @return the list of panel responses
     */
    @GetMapping("/panels")
    public ResponseEntity<ApiResponse<List<PanelResponse>>> getAllPanels() {
        List<PanelResponse> panels = panelRepository.findAll().stream()
                .map(p -> new PanelResponse(p.getId(), p.getFullName(), p.getEmail(),
                        p.getPhone(), p.getOrganization(), p.getDesignation()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(AppConstants.MSG_PANELS_FETCHED, panels));
    }

    /**
     * Maps an Application entity to a CandidateListResponse DTO.
     *
     * @param app the application entity
     * @return the candidate list response
     */
    private CandidateListResponse toCandidateListResponse(Application app) {
        CandidateListResponse candidateListResponse = new CandidateListResponse();
        candidateListResponse.setApplicationId(app.getId());
        candidateListResponse.setCandidateId(app.getCandidate().getId());
        candidateListResponse.setFullName(app.getCandidate().getFullName());
        candidateListResponse.setEmail(app.getCandidate().getEmail());
        candidateListResponse.setMobile(app.getCandidate().getMobile());
        candidateListResponse.setJobId(app.getJob().getId());
        candidateListResponse.setJobTitle(app.getJob().getTitle());
        candidateListResponse.setStage(app.getStage().name());
        candidateListResponse.setStatus(app.getStatus().name());
        candidateListResponse.setAppliedAt(app.getCreatedAt());
        return candidateListResponse;
    }

    /**
     * Builds a CandidateDetailResponse for an application, including all interviews.
     *
     * @param app the application entity
     * @param includeFeedback whether to include feedback data
     * @return the candidate detail response
     */
    private CandidateDetailResponse buildDetailResponse(Application app, boolean includeFeedback) {
        Candidate candidate = app.getCandidate();
        JobDescription jobDescription = app.getJob();

        CandidateDetailResponse candidateDetailResponse = new CandidateDetailResponse();
        candidateDetailResponse.setApplicationId(app.getId());
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
        candidateDetailResponse.setStage(app.getStage().name());
        candidateDetailResponse.setStatus(app.getStatus().name());
        candidateDetailResponse.setAppliedAt(app.getCreatedAt());

        List<Interview> interviews = interviewRepository.findByCandidate(candidate);
        candidateDetailResponse.setInterviews(interviews.stream()
                .map(i -> interviewService.toResponse(i, app))
                .collect(Collectors.toList()));

        if (includeFeedback) {
            List<FeedbackResponse> feedbacks = interviews.stream()
                    .flatMap(i -> feedbackService.getFeedbackByInterview(i.getId()).stream())
                    .collect(Collectors.toList());
            candidateDetailResponse.setFeedbacks(feedbacks);
        }

        return candidateDetailResponse;
    }
}
