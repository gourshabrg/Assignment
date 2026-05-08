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

    public Long getApplicationId() { return applicationId; }
    public Long getJobId() { return jobId; }
    public String getJobTitle() { return jobTitle; }
    public String getStatus() { return status; }
    public String getStage() { return stage; }
    public String getResumeUrl() { return resumeUrl; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
}
