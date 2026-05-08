package com.Capstone.InterviewTracking.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO representing a candidate's full profile, application state, and interview history.
 */
public class CandidateDetailResponse {

    private Long applicationId;
    private Long candidateId;
    private String fullName;
    private String email;
    private String mobile;
    private String currentCompany;
    private Double totalExperience;
    private Double relevantExperience;
    private Double currentCtc;
    private Double expectedCtc;
    private Integer noticePeriod;
    private String preferredLocation;
    private String source;
    private String resumeUrl;

    private Long jobId;
    private String jobTitle;
    private String jobDescription;
    private String skills;
    private String location;
    private String jobType;

    private String stage;
    private String status;
    private LocalDateTime appliedAt;

    /** All interview sessions scheduled for this candidate, across all rounds. */
    private List<InterviewResponse> interviews;

    /** Aggregated feedback from all completed rounds. */
    private List<FeedbackResponse> feedbacks;

    public CandidateDetailResponse() {}

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getCurrentCompany() { return currentCompany; }
    public void setCurrentCompany(String currentCompany) { this.currentCompany = currentCompany; }

    public Double getTotalExperience() { return totalExperience; }
    public void setTotalExperience(Double totalExperience) { this.totalExperience = totalExperience; }

    public Double getRelevantExperience() { return relevantExperience; }
    public void setRelevantExperience(Double relevantExperience) { this.relevantExperience = relevantExperience; }

    public Double getCurrentCtc() { return currentCtc; }
    public void setCurrentCtc(Double currentCtc) { this.currentCtc = currentCtc; }

    public Double getExpectedCtc() { return expectedCtc; }
    public void setExpectedCtc(Double expectedCtc) { this.expectedCtc = expectedCtc; }

    public Integer getNoticePeriod() { return noticePeriod; }
    public void setNoticePeriod(Integer noticePeriod) { this.noticePeriod = noticePeriod; }

    public String getPreferredLocation() { return preferredLocation; }
    public void setPreferredLocation(String preferredLocation) { this.preferredLocation = preferredLocation; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public List<InterviewResponse> getInterviews() { return interviews; }
    public void setInterviews(List<InterviewResponse> interviews) { this.interviews = interviews; }

    public List<FeedbackResponse> getFeedbacks() { return feedbacks; }
    public void setFeedbacks(List<FeedbackResponse> feedbacks) { this.feedbacks = feedbacks; }
}
