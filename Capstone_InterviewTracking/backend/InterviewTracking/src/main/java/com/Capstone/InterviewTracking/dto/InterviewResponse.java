package com.Capstone.InterviewTracking.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO representing a scheduled interview session.
 */
public class InterviewResponse {

    private Long id;
    private String round;
    private LocalDateTime interviewDateTime;
    private String focusArea;
    private String status;
    private List<PanelResponse> panels;
    private Long applicationId;
    private String candidateName;
    private String candidateEmail;

    public InterviewResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRound() { return round; }
    public void setRound(String round) { this.round = round; }

    public LocalDateTime getInterviewDateTime() { return interviewDateTime; }
    public void setInterviewDateTime(LocalDateTime interviewDateTime) { this.interviewDateTime = interviewDateTime; }

    public String getFocusArea() { return focusArea; }
    public void setFocusArea(String focusArea) { this.focusArea = focusArea; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<PanelResponse> getPanels() { return panels; }
    public void setPanels(List<PanelResponse> panels) { this.panels = panels; }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }
}
