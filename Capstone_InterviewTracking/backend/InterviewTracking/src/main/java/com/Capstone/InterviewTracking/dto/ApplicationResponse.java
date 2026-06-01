package com.Capstone.InterviewTracking.dto;

import java.time.LocalDateTime;

/**
 * Response DTO returned after a candidate successfully submits a job application.
 */
public class ApplicationResponse {

    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private String status;
    private String stage;
    private String resumeUrl;
    private LocalDateTime appliedAt;

    /**
     * Creates an ApplicationResponse with all fields.
     *
     * @param applicationId the application ID
     * @param jobId the job ID
     * @param jobTitle the job title
     * @param status the application status
     * @param stage the hiring stage
     * @param resumeUrl the resume URL
     * @param appliedAt the timestamp when the application was created
     */
    public ApplicationResponse(Long applicationId, Long jobId, String jobTitle,
                               String status, String stage, String resumeUrl,
                               LocalDateTime appliedAt) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.status = status;
        this.stage = stage;
        this.resumeUrl = resumeUrl;
        this.appliedAt = appliedAt;
    }

    /**
     * Returns the application ID.
     *
     * @return the application ID
     */
    public Long getApplicationId() { return applicationId; }

    /**
     * Sets the application ID.
     *
     * @param applicationId the application ID
     */
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    /**
     * Returns the job ID associated with this application.
     *
     * @return the job ID
     */
    public Long getJobId() { return jobId; }

    /**
     * Sets the job ID associated with this application.
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
     * Returns the current hiring stage of the candidate.
     *
     * @return the hiring stage
     */
    public String getStage() { return stage; }

    /**
     * Sets the current hiring stage of the candidate.
     *
     * @param stage the hiring stage
     */
    public void setStage(String stage) { this.stage = stage; }

    /**
     * Returns the URL of the uploaded resume on Google Drive.
     *
     * @return the resume URL
     */
    public String getResumeUrl() { return resumeUrl; }

    /**
     * Sets the URL of the uploaded resume.
     *
     * @param resumeUrl the resume URL
     */
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }

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
}
