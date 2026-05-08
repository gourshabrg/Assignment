package com.Capstone.InterviewTracking.controller;

import com.Capstone.InterviewTracking.constant.AppConstants;
import com.Capstone.InterviewTracking.dto.*;
import com.Capstone.InterviewTracking.entity.*;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

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
        return ResponseEntity.ok(ApiResponse.success("Panel created successfully. Email sent.", null));
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

        List<CandidateListResponse> result = applications.stream()
                .map(this::toCandidateListResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Candidates fetched", result));
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

        CandidateDetailResponse response = buildDetailResponse(application, true);
        return ResponseEntity.ok(ApiResponse.success("Candidate detail fetched", response));
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
        InterviewResponse response = interviewService.scheduleInterview(request, hrEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Interview scheduled successfully", response));
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

        List<InterviewResponse> result = hrInterviews.stream()
                .map(i -> interviewService.toResponse(i, null))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("HR interviews fetched", result));
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

        CandidateDetailResponse response = buildDetailResponse(apps.get(0), false);
        return ResponseEntity.ok(ApiResponse.success("Candidate detail fetched", response));
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

        FeedbackResponse response = feedbackService.submitHRFeedback(
                interviewId, authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("HR feedback submitted successfully", response));
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
        return ResponseEntity.ok(ApiResponse.success("Feedback fetched", feedbacks));
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

        return ResponseEntity.ok(ApiResponse.success("Candidate rejected", null));
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

        return ResponseEntity.ok(ApiResponse.success("Candidate selected", null));
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

        return ResponseEntity.ok(ApiResponse.success("Panels fetched", panels));
    }

    /**
     * Maps an Application entity to a CandidateListResponse DTO.
     *
     * @param app the application entity
     * @return the candidate list response
     */
    private CandidateListResponse toCandidateListResponse(Application app) {
        CandidateListResponse r = new CandidateListResponse();
        r.setApplicationId(app.getId());
        r.setCandidateId(app.getCandidate().getId());
        r.setFullName(app.getCandidate().getFullName());
        r.setEmail(app.getCandidate().getEmail());
        r.setMobile(app.getCandidate().getMobile());
        r.setJobId(app.getJob().getId());
        r.setJobTitle(app.getJob().getTitle());
        r.setStage(app.getStage().name());
        r.setStatus(app.getStatus().name());
        r.setAppliedAt(app.getCreatedAt());
        return r;
    }

    /**
     * Builds a CandidateDetailResponse for an application, including all interviews.
     *
     * @param app the application entity
     * @param includeFeedback whether to include feedback data
     * @return the candidate detail response
     */
    private CandidateDetailResponse buildDetailResponse(Application app, boolean includeFeedback) {
        Candidate c = app.getCandidate();
        JobDescription jd = app.getJob();

        CandidateDetailResponse r = new CandidateDetailResponse();
        r.setApplicationId(app.getId());
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
        r.setStage(app.getStage().name());
        r.setStatus(app.getStatus().name());
        r.setAppliedAt(app.getCreatedAt());

        List<Interview> interviews = interviewRepository.findByCandidate(c);
        r.setInterviews(interviews.stream()
                .map(i -> interviewService.toResponse(i, app))
                .collect(Collectors.toList()));

        if (includeFeedback) {
            List<FeedbackResponse> feedbacks = interviews.stream()
                    .flatMap(i -> feedbackService.getFeedbackByInterview(i.getId()).stream())
                    .collect(Collectors.toList());
            r.setFeedbacks(feedbacks);
        }

        return r;
    }
}
