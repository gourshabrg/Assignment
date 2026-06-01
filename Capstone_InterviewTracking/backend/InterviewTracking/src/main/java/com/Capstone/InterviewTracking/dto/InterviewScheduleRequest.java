package com.Capstone.InterviewTracking.dto;

import com.Capstone.InterviewTracking.enums.InterviewRound;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for scheduling an interview for a candidate.
 */
public class InterviewScheduleRequest {

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotNull(message = "Interview round is required")
    private InterviewRound round;

    @NotNull(message = "Interview date and time is required")
    private LocalDateTime interviewDateTime;

    /** Technical or behavioural focus areas to be covered in this session. */
    private String focusArea;

    /** IDs of the panel members assigned to this interview. */
    private List<Long> panelIds;

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public InterviewRound getRound() { return round; }
    public void setRound(InterviewRound round) { this.round = round; }

    public LocalDateTime getInterviewDateTime() { return interviewDateTime; }
    public void setInterviewDateTime(LocalDateTime interviewDateTime) { this.interviewDateTime = interviewDateTime; }

    public String getFocusArea() { return focusArea; }
    public void setFocusArea(String focusArea) { this.focusArea = focusArea; }

    public List<Long> getPanelIds() { return panelIds; }
    public void setPanelIds(List<Long> panelIds) { this.panelIds = panelIds; }
}
