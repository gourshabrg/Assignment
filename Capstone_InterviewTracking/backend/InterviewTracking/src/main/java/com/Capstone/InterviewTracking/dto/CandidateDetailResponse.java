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

    /**
     * Returns the ID of the candidate's application.
     *
     * @return the application ID
     */
    public Long getApplicationId() { return applicationId; }

    /**
     * Sets the ID of the candidate's application.
     *
     * @param applicationId the application ID
     */
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    /**
     * Returns the candidate's unique ID.
     *
     * @return the candidate ID
     */
    public Long getCandidateId() { return candidateId; }

    /**
     * Sets the candidate's unique ID.
     *
     * @param candidateId the candidate ID
     */
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }

    /**
     * Returns the candidate's full name.
     *
     * @return the full name
     */
    public String getFullName() { return fullName; }

    /**
     * Sets the candidate's full name.
     *
     * @param fullName the full name
     */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /**
     * Returns the candidate's email address.
     *
     * @return the email address
     */
    public String getEmail() { return email; }

    /**
     * Sets the candidate's email address.
     *
     * @param email the email address
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Returns the candidate's mobile number.
     *
     * @return the mobile number
     */
    public String getMobile() { return mobile; }

    /**
     * Sets the candidate's mobile number.
     *
     * @param mobile the mobile number
     */
    public void setMobile(String mobile) { this.mobile = mobile; }

    /**
     * Returns the candidate's current employer.
     *
     * @return the current company name
     */
    public String getCurrentCompany() { return currentCompany; }

    /**
     * Sets the candidate's current employer.
     *
     * @param currentCompany the current company name
     */
    public void setCurrentCompany(String currentCompany) { this.currentCompany = currentCompany; }

    /**
     * Returns the candidate's total years of work experience.
     *
     * @return the total experience in years
     */
    public Double getTotalExperience() { return totalExperience; }

    /**
     * Sets the candidate's total years of work experience.
     *
     * @param totalExperience the total experience in years
     */
    public void setTotalExperience(Double totalExperience) { this.totalExperience = totalExperience; }

    /**
     * Returns the candidate's years of relevant work experience.
     *
     * @return the relevant experience in years
     */
    public Double getRelevantExperience() { return relevantExperience; }

    /**
     * Sets the candidate's years of relevant work experience.
     *
     * @param relevantExperience the relevant experience in years
     */
    public void setRelevantExperience(Double relevantExperience) { this.relevantExperience = relevantExperience; }

    /**
     * Returns the candidate's current annual CTC.
     *
     * @return the current CTC
     */
    public Double getCurrentCtc() { return currentCtc; }

    /**
     * Sets the candidate's current annual CTC.
     *
     * @param currentCtc the current CTC
     */
    public void setCurrentCtc(Double currentCtc) { this.currentCtc = currentCtc; }

    /**
     * Returns the candidate's expected annual CTC.
     *
     * @return the expected CTC
     */
    public Double getExpectedCtc() { return expectedCtc; }

    /**
     * Sets the candidate's expected annual CTC.
     *
     * @param expectedCtc the expected CTC
     */
    public void setExpectedCtc(Double expectedCtc) { this.expectedCtc = expectedCtc; }

    /**
     * Returns the candidate's notice period in days.
     *
     * @return the notice period
     */
    public Integer getNoticePeriod() { return noticePeriod; }

    /**
     * Sets the candidate's notice period in days.
     *
     * @param noticePeriod the notice period
     */
    public void setNoticePeriod(Integer noticePeriod) { this.noticePeriod = noticePeriod; }

    /**
     * Returns the candidate's preferred work location.
     *
     * @return the preferred location
     */
    public String getPreferredLocation() { return preferredLocation; }

    /**
     * Sets the candidate's preferred work location.
     *
     * @param preferredLocation the preferred location
     */
    public void setPreferredLocation(String preferredLocation) { this.preferredLocation = preferredLocation; }

    /**
     * Returns the recruitment channel or referral source for the candidate.
     *
     * @return the source
     */
    public String getSource() { return source; }

    /**
     * Sets the recruitment channel or referral source for the candidate.
     *
     * @param source the source
     */
    public void setSource(String source) { this.source = source; }

    /**
     * Returns the Google Drive URL for the candidate's uploaded resume.
     *
     * @return the resume URL
     */
    public String getResumeUrl() { return resumeUrl; }

    /**
     * Sets the Google Drive URL for the candidate's uploaded resume.
     *
     * @param resumeUrl the resume URL
     */
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }

    /**
     * Returns the ID of the job the candidate applied for.
     *
     * @return the job ID
     */
    public Long getJobId() { return jobId; }

    /**
     * Sets the ID of the job the candidate applied for.
     *
     * @param jobId the job ID
     */
    public void setJobId(Long jobId) { this.jobId = jobId; }

    /**
     * Returns the title of the applied job.
     *
     * @return the job title
     */
    public String getJobTitle() { return jobTitle; }

    /**
     * Sets the title of the applied job.
     *
     * @param jobTitle the job title
     */
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    /**
     * Returns the full description of the applied job.
     *
     * @return the job description
     */
    public String getJobDescription() { return jobDescription; }

    /**
     * Sets the full description of the applied job.
     *
     * @param jobDescription the job description
     */
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }

    /**
     * Returns the required skills for the applied job.
     *
     * @return the required skills
     */
    public String getSkills() { return skills; }

    /**
     * Sets the required skills for the applied job.
     *
     * @param skills the required skills
     */
    public void setSkills(String skills) { this.skills = skills; }

    /**
     * Returns the job location.
     *
     * @return the job location
     */
    public String getLocation() { return location; }

    /**
     * Sets the job location.
     *
     * @param location the job location
     */
    public void setLocation(String location) { this.location = location; }

    /**
     * Returns the job type (e.g., FULL_TIME, CONTRACT, REMOTE).
     *
     * @return the job type
     */
    public String getJobType() { return jobType; }

    /**
     * Sets the job type.
     *
     * @param jobType the job type
     */
    public void setJobType(String jobType) { this.jobType = jobType; }

    /**
     * Returns the candidate's current hiring stage (e.g., PROFILING, L1, HR).
     *
     * @return the hiring stage
     */
    public String getStage() { return stage; }

    /**
     * Sets the candidate's current hiring stage.
     *
     * @param stage the hiring stage
     */
    public void setStage(String stage) { this.stage = stage; }

    /**
     * Returns the current application status (e.g., APPLIED, SELECTED, REJECTED).
     *
     * @return the application status
     */
    public String getStatus() { return status; }

    /**
     * Sets the current application status.
     *
     * @param status the application status
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Returns the timestamp when the application was submitted.
     *
     * @return the applied-at timestamp
     */
    public LocalDateTime getAppliedAt() { return appliedAt; }

    /**
     * Sets the timestamp when the application was submitted.
     *
     * @param appliedAt the applied-at timestamp
     */
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    /**
     * Returns all interview sessions scheduled for this candidate across all rounds.
     *
     * @return the list of interview responses
     */
    public List<InterviewResponse> getInterviews() { return interviews; }

    /**
     * Sets all interview sessions for this candidate.
     *
     * @param interviews the list of interview responses
     */
    public void setInterviews(List<InterviewResponse> interviews) { this.interviews = interviews; }

    /**
     * Returns the aggregated feedback from all completed interview rounds.
     *
     * @return the list of feedback responses
     */
    public List<FeedbackResponse> getFeedbacks() { return feedbacks; }

    /**
     * Sets the aggregated feedback from all completed interview rounds.
     *
     * @param feedbacks the list of feedback responses
     */
    public void setFeedbacks(List<FeedbackResponse> feedbacks) { this.feedbacks = feedbacks; }
}
