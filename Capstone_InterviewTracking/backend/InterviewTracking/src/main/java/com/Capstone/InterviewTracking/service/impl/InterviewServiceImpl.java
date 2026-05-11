package com.Capstone.InterviewTracking.service.impl;

import com.Capstone.InterviewTracking.dto.InterviewResponse;
import com.Capstone.InterviewTracking.dto.InterviewScheduleRequest;
import com.Capstone.InterviewTracking.dto.PanelResponse;
import com.Capstone.InterviewTracking.entity.Application;
import com.Capstone.InterviewTracking.entity.Candidate;
import com.Capstone.InterviewTracking.entity.Interview;
import com.Capstone.InterviewTracking.entity.Panel;
import com.Capstone.InterviewTracking.enums.InterviewRound;
import com.Capstone.InterviewTracking.enums.InterviewStage;
import com.Capstone.InterviewTracking.enums.InterviewStatus;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import com.Capstone.InterviewTracking.repository.ApplicationRepository;
import com.Capstone.InterviewTracking.repository.InterviewRepository;
import com.Capstone.InterviewTracking.repository.PanelRepository;
import com.Capstone.InterviewTracking.service.EmailService;
import com.Capstone.InterviewTracking.service.InterviewService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of InterviewService that handles interview scheduling and retrieval.
 */
@Service
public class InterviewServiceImpl implements InterviewService {

    private final ApplicationRepository applicationRepository;
    private final PanelRepository panelRepository;
    private final InterviewRepository interviewRepository;
    private final EmailService emailService;

    /**
     * Creates an InterviewServiceImpl with the required dependencies.
     *
     * @param applicationRepository the application repository
     * @param panelRepository the panel repository
     * @param interviewRepository the interview repository
     * @param emailService the email service
     */
    public InterviewServiceImpl(ApplicationRepository applicationRepository,
                                PanelRepository panelRepository,
                                InterviewRepository interviewRepository,
                                EmailService emailService) {
        this.applicationRepository = applicationRepository;
        this.panelRepository = panelRepository;
        this.interviewRepository = interviewRepository;
        this.emailService = emailService;
    }

    /**
     * Schedules an interview for a candidate and sends notification emails.
     *
     * @param request the schedule request details
     * @param hrEmail the HR user's email for HR-round notifications
     * @return the created interview response
     */
    @Override
    public InterviewResponse scheduleInterview(InterviewScheduleRequest request, String hrEmail) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new BadRequestException("Application not found"));

        Candidate candidate = application.getCandidate();

        Map<InterviewStage, InterviewRound> allowedRoundForStage = Map.of(
                InterviewStage.PROFILING, InterviewRound.SCREENING,
                InterviewStage.SCREENING, InterviewRound.L1,
                InterviewStage.L1,        InterviewRound.L2,
                InterviewStage.L2,        InterviewRound.HR
        );

        InterviewRound expectedRound = allowedRoundForStage.get(application.getStage());
        if (expectedRound == null) {
            throw new BadRequestException(
                    "Cannot schedule an interview — candidate is at " + application.getStage().name()
                    + " stage with no further rounds available."
            );
        }
        if (request.getRound() != expectedRound) {
            throw new BadRequestException(
                    "Cannot schedule " + request.getRound().name() + " interview. "
                    + "Candidate is at " + application.getStage().name() + " stage. "
                    + "Expected round: " + expectedRound.name()
            );
        }

        if (interviewRepository.existsByCandidateAndRound(candidate, request.getRound())) {
            throw new BadRequestException("Interview for this round is already scheduled for this candidate");
        }

        boolean isHrRound = request.getRound() == InterviewRound.HR;
        List<Panel> panels = new ArrayList<>();

        if (!isHrRound) {
            List<Long> panelIds = request.getPanelIds();
            if (panelIds == null || panelIds.isEmpty()) {
                throw new BadRequestException("At least one panel member is required for this round");
            }
            if (panelIds.size() > 2) {
                throw new BadRequestException("Maximum 2 panel members allowed per interview");
            }
            panels = panelRepository.findAllById(panelIds);
            if (panels.isEmpty()) {
                throw new BadRequestException("No valid panel members found");
            }
        }

        Interview interview = new Interview();
        interview.setCandidate(candidate);
        interview.setRound(request.getRound());
        interview.setInterviewDateTime(request.getInterviewDateTime());
        interview.setFocusArea(request.getFocusArea());
        interview.setStatus(InterviewStatus.SCHEDULED);
        interview.setPanels(panels);

        interview = interviewRepository.save(interview);

        String interviewerLabel = isHrRound ? "HR Team"
                : panels.stream().map(Panel::getFullName).collect(Collectors.joining(", "));

        emailService.sendInterviewScheduledMail(
                candidate.getEmail(),
                candidate.getFullName(),
                request.getRound().name(),
                request.getInterviewDateTime().toString(),
                interviewerLabel
        );

        if (isHrRound && Objects.nonNull(hrEmail)) {
            emailService.sendHRInterviewScheduledMail(
                    hrEmail,
                    candidate.getFullName(),
                    candidate.getEmail(),
                    request.getInterviewDateTime().toString()
            );
        }

        return toResponse(interview, application);
    }

    /**
     * Returns all interviews assigned to the given panel member.
     *
     * @param panelEmail the panel member's email
     * @return list of interview responses
     */
    @Override
    public List<InterviewResponse> getInterviewsByPanel(String panelEmail) {
        Panel panel = panelRepository.findByEmail(panelEmail)
                .orElseThrow(() -> new BadRequestException("Panel member not found"));

        return interviewRepository.findByPanelsContaining(panel)
                .stream()
                .map(i -> toResponse(i, null))
                .collect(Collectors.toList());
    }

    /**
     * Returns all interviews for a specific candidate.
     *
     * @param candidateId the candidate's ID
     * @return list of interview responses
     */
    @Override
    public List<InterviewResponse> getInterviewsByCandidate(Long candidateId) {
        return interviewRepository.findAll()
                .stream()
                .filter(i -> i.getCandidate().getId().equals(candidateId))
                .map(i -> toResponse(i, null))
                .collect(Collectors.toList());
    }

    /**
     * Converts an Interview entity to an InterviewResponse DTO with no application context.
     *
     * @param interview the interview entity
     * @return the interview response
     */
    public InterviewResponse toResponse(Interview interview) {
        return toResponse(interview, null);
    }

    /**
     * Converts an Interview entity to an InterviewResponse DTO.
     *
     * @param interview the interview entity
     * @param application the associated application, or null to resolve automatically
     * @return the interview response
     */
    public InterviewResponse toResponse(Interview interview, Application application) {
        InterviewResponse response = new InterviewResponse();
        response.setId(interview.getId());
        response.setRound(interview.getRound().name());
        response.setInterviewDateTime(interview.getInterviewDateTime());
        response.setFocusArea(interview.getFocusArea());
        response.setStatus(interview.getStatus().name());

        List<PanelResponse> panelResponses = interview.getPanels().stream()
                .map(p -> new PanelResponse(p.getId(), p.getFullName(), p.getEmail(),
                        p.getPhone(), p.getOrganization(), p.getDesignation()))
                .collect(Collectors.toList());
        response.setPanels(panelResponses);

        Candidate candidate = interview.getCandidate();
        if (Objects.nonNull(candidate)) {
            response.setCandidateName(candidate.getFullName());
            response.setCandidateEmail(candidate.getEmail());
        }

        if (Objects.nonNull(application)) {
            response.setApplicationId(application.getId());
        } else if (Objects.nonNull(candidate)) {
            applicationRepository.findByCandidateOrderByCreatedAtDesc(candidate)
                    .stream().findFirst()
                    .ifPresent(app -> response.setApplicationId(app.getId()));
        }

        return response;
    }
}
