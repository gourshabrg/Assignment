package com.Capstone.InterviewTracking.entity;

import com.Capstone.InterviewTracking.enums.*;
import jakarta.persistence.*;

@Entity
@Table(name = "candidate")
public class Candidate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String mobile;
    private String resumeUrl;

    private String currentCompany;
    private Double totalExperience;
    private Double relevantExperience;

    private Double currentCtc;
    private Double expectedCtc;

    private Integer noticePeriod;
    private String preferredLocation;
    private String source;

    @ManyToOne
    @JoinColumn(name = "jd_id")
    private JobDescription jobDescription;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private InterviewStage stage;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }

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

    public JobDescription getJobDescription() { return jobDescription; }
    public void setJobDescription(JobDescription jobDescription) { this.jobDescription = jobDescription; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public InterviewStage getStage() { return stage; }
    public void setStage(InterviewStage stage) { this.stage = stage; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
}