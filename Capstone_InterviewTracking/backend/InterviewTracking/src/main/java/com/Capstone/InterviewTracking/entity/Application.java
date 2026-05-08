package com.Capstone.InterviewTracking.entity;

import com.Capstone.InterviewTracking.enums.*;
import jakarta.persistence.*;

/**
 * Represents a candidate's application for a job opening.
 */
@Entity
@Table(name = "application")
public class Application extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private JobDescription job;

    @Enumerated(EnumType.STRING)
    private InterviewStage stage;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    public Long getId() { return id; }

    /**
     * Returns the candidate for this application.
     *
     * @return the candidate
     */
    public Candidate getCandidate() { return candidate; }

    /**
     * Sets the candidate for this application.
     *
     * @param candidate the candidate
     */
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }

    /**
     * Returns the job this application is for.
     *
     * @return the job description
     */
    public JobDescription getJob() { return job; }

    /**
     * Sets the job for this application.
     *
     * @param job the job description
     */
    public void setJob(JobDescription job) { this.job = job; }

    /**
     * Returns the current hiring stage of the candidate.
     *
     * @return the interview stage
     */
    public InterviewStage getStage() { return stage; }

    /**
     * Sets the hiring stage of the candidate.
     *
     * @param stage the new interview stage
     */
    public void setStage(InterviewStage stage) { this.stage = stage; }

    /**
     * Returns the current status of the application.
     *
     * @return the application status
     */
    public ApplicationStatus getStatus() { return status; }

    /**
     * Sets the status of the application.
     *
     * @param status the new application status
     */
    public void setStatus(ApplicationStatus status) { this.status = status; }
}
