package com.Capstone.InterviewTracking.dto;

import jakarta.validation.constraints.*;

/**
 * Request DTO for creating or updating a candidate profile.
 */
public class CandidateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Mobile is required")
    private String mobile;

    @NotNull(message = "Job ID is required")
    private Long jobId;

    private String currentCompany;
    private Double totalExperience;
    private Double relevantExperience;
    private Double currentCtc;
    private Double expectedCtc;
    private Integer noticePeriod;
    private String preferredLocation;
    private String source;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

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
}
