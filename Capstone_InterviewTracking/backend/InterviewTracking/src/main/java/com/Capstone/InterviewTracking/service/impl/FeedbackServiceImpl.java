package com.Capstone.InterviewTracking.service.impl;

import com.Capstone.InterviewTracking.dto.FeedbackRequest;
import com.Capstone.InterviewTracking.dto.FeedbackResponse;
import com.Capstone.InterviewTracking.entity.Feedback;
import com.Capstone.InterviewTracking.entity.Interview;
import com.Capstone.InterviewTracking.entity.Panel;
import com.Capstone.InterviewTracking.enums.InterviewRound;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import com.Capstone.InterviewTracking.repository.FeedbackRepository;
import com.Capstone.InterviewTracking.repository.InterviewRepository;
import com.Capstone.InterviewTracking.repository.PanelRepository;
import com.Capstone.InterviewTracking.service.FeedbackService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of FeedbackService that saves and retrieves interview feedback.
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final InterviewRepository interviewRepository;
    private final PanelRepository panelRepository;

    /**
     * Creates a FeedbackServiceImpl with the required repositories.
     *
     * @param feedbackRepository the feedback repository
     * @param interviewRepository the interview repository
     * @param panelRepository the panel repository
     */
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository,
                               InterviewRepository interviewRepository,
                               PanelRepository panelRepository) {
        this.feedbackRepository = feedbackRepository;
        this.interviewRepository = interviewRepository;
        this.panelRepository = panelRepository;
    }

    /**
     * Submits feedback from a panel member for an interview.
     *
     * @param interviewId the interview ID
     * @param panelEmail the panel member's email
     * @param request the feedback details
     * @return the saved feedback response
     */
    @Override
    public FeedbackResponse submitFeedback(Long interviewId, String panelEmail, FeedbackRequest request) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new BadRequestException("Interview not found"));

        Panel panel = panelRepository.findByEmail(panelEmail)
                .orElseThrow(() -> new BadRequestException("Panel member not found"));

        boolean isAssigned = interview.getPanels().stream()
                .anyMatch(p -> p.getId().equals(panel.getId()));

        if (!isAssigned) {
            throw new BadRequestException("You are not assigned to this interview");
        }

        if (feedbackRepository.existsByInterviewAndPanel(interview, panel)) {
            throw new BadRequestException("Feedback already submitted for this interview");
        }

        Feedback feedback = new Feedback();
        feedback.setInterview(interview);
        feedback.setPanel(panel);
        feedback.setComments(request.getComments());
        feedback.setStrengths(request.getStrengths());
        feedback.setWeaknesses(request.getWeaknesses());
        feedback.setAreasCovered(request.getAreasCovered());
        feedback.setRating(request.getRating());
        feedback.setStatus(request.getStatus());

        feedback = feedbackRepository.save(feedback);
        return toResponse(feedback);
    }

    /**
     * Submits HR feedback for an HR-round interview.
     *
     * @param interviewId the interview ID
     * @param hrEmail the HR reviewer's email
     * @param request the feedback details
     * @return the saved feedback response
     */
    @Override
    public FeedbackResponse submitHRFeedback(Long interviewId, String hrEmail, FeedbackRequest request) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new BadRequestException("Interview not found"));

        if (interview.getRound() != InterviewRound.HR) {
            throw new BadRequestException("This endpoint is only for HR round interviews");
        }

        if (feedbackRepository.existsByInterviewAndHrReviewer(interview, hrEmail)) {
            throw new BadRequestException("Feedback already submitted for this interview");
        }

        Feedback feedback = new Feedback();
        feedback.setInterview(interview);
        feedback.setPanel(null);
        feedback.setHrReviewer(hrEmail);
        feedback.setComments(request.getComments());
        feedback.setStrengths(request.getStrengths());
        feedback.setWeaknesses(request.getWeaknesses());
        feedback.setAreasCovered(request.getAreasCovered());
        feedback.setRating(request.getRating());
        feedback.setStatus(request.getStatus());

        feedback = feedbackRepository.save(feedback);
        return toResponse(feedback);
    }

    /**
     * Returns all feedback records for a specific interview.
     *
     * @param interviewId the interview ID
     * @return list of feedback responses
     */
    @Override
    public List<FeedbackResponse> getFeedbackByInterview(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new BadRequestException("Interview not found"));

        return feedbackRepository.findByInterview(interview)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Feedback entity to a FeedbackResponse DTO.
     *
     * @param feedback the feedback entity
     * @return the feedback response
     */
    public FeedbackResponse toResponse(Feedback feedback) {
        FeedbackResponse response = new FeedbackResponse();
        response.setId(feedback.getId());
        response.setInterviewId(feedback.getInterview().getId());
        response.setRound(feedback.getInterview().getRound().name());

        if (Objects.nonNull(feedback.getPanel())) {
            response.setPanelName(feedback.getPanel().getFullName());
        } else {
            response.setPanelName(Objects.nonNull(feedback.getHrReviewer()) ? feedback.getHrReviewer() : "HR");
        }

        response.setComments(feedback.getComments());
        response.setStrengths(feedback.getStrengths());
        response.setWeaknesses(feedback.getWeaknesses());
        response.setAreasCovered(feedback.getAreasCovered());
        response.setRating(feedback.getRating());
        response.setStatus(feedback.getStatus().name());
        return response;
    }
}
