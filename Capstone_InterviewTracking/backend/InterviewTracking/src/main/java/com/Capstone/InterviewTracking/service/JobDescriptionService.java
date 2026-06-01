package com.Capstone.InterviewTracking.service;

import com.Capstone.InterviewTracking.dto.JobDescriptionRequest;
import com.Capstone.InterviewTracking.dto.JobDescriptionResponse;

import java.util.List;

/**
 * Service interface for managing job descriptions.
 */
public interface JobDescriptionService {

    /**
     * Creates a new job description.
     *
     * @param request the job details
     * @param createdByEmail the HR user's email
     * @return the created job description response
     */
    JobDescriptionResponse create(JobDescriptionRequest request, String createdByEmail);

    /**
     * Updates an existing job description.
     *
     * @param id the job ID
     * @param request the updated job details
     * @param email the HR user's email
     * @return the updated job description response
     */
    JobDescriptionResponse update(Long id, JobDescriptionRequest request, String email);

    /**
     * Permanently deletes a job description.
     *
     * @param id the job ID
     * @param email the HR user's email
     */
    void delete(Long id, String email);

    /**
     * Returns a job description by ID.
     *
     * @param id the job ID
     * @return the job description response
     */
    JobDescriptionResponse getById(Long id);

    /**
     * Returns all active job descriptions for the public listing.
     *
     * @return list of active job descriptions
     */
    List<JobDescriptionResponse> findActiveJobs();

    /**
     * Returns all job descriptions for the HR management view.
     *
     * @return list of all job descriptions
     */
    List<JobDescriptionResponse> findAllForHR();

    /**
     * Toggles the active or inactive state of a job.
     *
     * @param id the job ID
     * @param email the HR user's email
     */
    void toggleActive(Long id, String email);
}
