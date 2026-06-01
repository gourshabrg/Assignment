package com.Capstone.InterviewTracking.repository;

import com.Capstone.InterviewTracking.entity.Application;
import com.Capstone.InterviewTracking.entity.Candidate;
import com.Capstone.InterviewTracking.entity.JobDescription;
import com.Capstone.InterviewTracking.enums.ApplicationStatus;
import com.Capstone.InterviewTracking.enums.InterviewStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Application entities.
 */
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /**
     * Checks if a candidate has an application with the given status.
     *
     * @param candidate the candidate
     * @param status the application status
     * @return true if such an application exists
     */
    boolean existsByCandidateAndStatus(Candidate candidate, ApplicationStatus status);

    /**
     * Checks if a candidate has already applied for a specific job.
     *
     * @param candidate the candidate
     * @param job the job description
     * @return true if the candidate has applied for that job
     */
    boolean existsByCandidateAndJob(Candidate candidate, JobDescription job);

    /**
     * Finds the application for the given candidate.
     *
     * @param candidate the candidate
     * @return the application if found
     */
    Optional<Application> findByCandidate(Candidate candidate);

    /**
     * Returns all applications for a candidate ordered by creation date descending.
     *
     * @param candidate the candidate
     * @return list of applications
     */
    List<Application> findByCandidateOrderByCreatedAtDesc(Candidate candidate);

    /**
     * Returns all applications ordered by creation date descending.
     *
     * @return list of all applications
     */
    List<Application> findAllByOrderByCreatedAtDesc();

    /**
     * Returns all applications at the given hiring stage.
     *
     * @param stage the interview stage
     * @return matching applications
     */
    List<Application> findByStage(InterviewStage stage);

    /**
     * Returns all applications with the given status.
     *
     * @param status the application status
     * @return matching applications
     */
    List<Application> findByStatus(ApplicationStatus status);

    /**
     * Returns all applications for the given job.
     *
     * @param job the job description
     * @return matching applications
     */
    List<Application> findByJob(JobDescription job);

    /**
     * Returns all applications matching the given stage and status.
     *
     * @param stage the interview stage
     * @param status the application status
     * @return matching applications
     */
    List<Application> findByStageAndStatus(InterviewStage stage, ApplicationStatus status);

    /**
     * Checks if a job has any application whose status is not the given value.
     *
     * @param job the job description
     * @param status the status to exclude
     * @return true if at least one application with a different status exists
     */
    boolean existsByJobAndStatusNot(JobDescription job, ApplicationStatus status);

    /**
     * Checks if any application exists for the given job.
     *
     * @param job the job description
     * @return true if at least one application exists
     */
    boolean existsByJob(JobDescription job);
}
