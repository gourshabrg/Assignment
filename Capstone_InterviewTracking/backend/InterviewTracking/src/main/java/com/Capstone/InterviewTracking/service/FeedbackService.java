package com.Capstone.InterviewTracking.service;

import com.Capstone.InterviewTracking.dto.FeedbackRequest;
import com.Capstone.InterviewTracking.dto.FeedbackResponse;

import java.util.List;

/**
 * Service interface for submitting and retrieving interview feedback.
 */
public interface FeedbackService {

    /**
     * Submits feedback from a panel member for an interview.
     *
     * @param interviewId the interview ID
     * @param panelEmail the panel member's email
     * @param request the feedback details
     * @return the saved feedback response
     */
    FeedbackResponse submitFeedback(Long interviewId, String panelEmail, FeedbackRequest request);

    /**
     * Returns all feedback records for a specific interview.
     *
     * @param interviewId the interview ID
     * @return list of feedback responses
     */
    List<FeedbackResponse> getFeedbackByInterview(Long interviewId);

    /**
     * Submits HR feedback for an HR-round interview.
     *
     * @param interviewId the interview ID
     * @param hrEmail the HR reviewer's email
     * @param request the feedback details
     * @return the saved feedback response
     */
    FeedbackResponse submitHRFeedback(Long interviewId, String hrEmail, FeedbackRequest request);
}
