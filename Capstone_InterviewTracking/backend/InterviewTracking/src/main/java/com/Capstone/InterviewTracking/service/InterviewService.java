package com.Capstone.InterviewTracking.service;

import com.Capstone.InterviewTracking.dto.InterviewResponse;
import com.Capstone.InterviewTracking.dto.InterviewScheduleRequest;
import com.Capstone.InterviewTracking.entity.Application;
import com.Capstone.InterviewTracking.entity.Interview;

import java.util.List;

/**
 * Service interface for scheduling and querying candidate interviews.
 */
public interface InterviewService {

    /**
     * Schedules an interview for a candidate and sends notification emails.
     *
     * @param request the schedule request details
     * @param hrEmail the HR user's email for HR-round notifications
     * @return the created interview response
     */
    InterviewResponse scheduleInterview(InterviewScheduleRequest request, String hrEmail);

    /**
     * Returns all interviews assigned to the given panel member.
     *
     * @param panelEmail the panel member's email
     * @return list of interview responses
     */
    List<InterviewResponse> getInterviewsByPanel(String panelEmail);

    /**
     * Returns all interviews for a specific candidate.
     *
     * @param candidateId the candidate's ID
     * @return list of interview responses
     */
    List<InterviewResponse> getInterviewsByCandidate(Long candidateId);

    /**
     * Converts an Interview entity to an InterviewResponse DTO.
     *
     * @param interview the interview entity
     * @param application the associated application, or null to resolve automatically
     * @return the interview response
     */
    InterviewResponse toResponse(Interview interview, Application application);
}
