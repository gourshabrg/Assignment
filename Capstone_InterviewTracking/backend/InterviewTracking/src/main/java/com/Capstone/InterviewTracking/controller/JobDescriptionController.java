package com.Capstone.InterviewTracking.controller;

import com.Capstone.InterviewTracking.constant.AppConstants;
import com.Capstone.InterviewTracking.dto.ApiResponse;
import com.Capstone.InterviewTracking.dto.JobDescriptionRequest;
import com.Capstone.InterviewTracking.dto.JobDescriptionResponse;
import com.Capstone.InterviewTracking.service.JobDescriptionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing job descriptions.
 */
@RestController
@RequestMapping(AppConstants.JOB_BASE_PATH)
public class JobDescriptionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobDescriptionController.class);

    private final JobDescriptionService jobDescriptionService;

    /**
     * Creates a JobDescriptionController with the required service.
     *
     * @param jobDescriptionService the job description service
     */
    public JobDescriptionController(JobDescriptionService jobDescriptionService) {
        this.jobDescriptionService = jobDescriptionService;
    }

    /**
     * Creates a new job description posted by the authenticated HR user.
     *
     * @param request the job details
     * @param authentication the authenticated HR user
     * @return the created job description response
     */
    @PostMapping
    public ResponseEntity<ApiResponse<JobDescriptionResponse>> create(
            @Valid @RequestBody JobDescriptionRequest request,
            Authentication authentication) {
        LOGGER.info("Create job description request received for title: {}", request.getTitle());

        JobDescriptionResponse response = jobDescriptionService.create(request, authentication.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job description created successfully", response));
    }

    /**
     * Returns all currently active job descriptions.
     *
     * @return the list of active job descriptions
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobDescriptionResponse>>> findActiveJobs() {
        LOGGER.info("Fetch active job descriptions request received");

        List<JobDescriptionResponse> response = jobDescriptionService.findActiveJobs();
        return ResponseEntity.ok(ApiResponse.success("Job descriptions fetched successfully", response));
    }

    /**
     * Updates an existing job description.
     *
     * @param id the job ID
     * @param request the updated job details
     * @param authentication the authenticated HR user
     * @return the updated job description response
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDescriptionResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody JobDescriptionRequest request,
            Authentication authentication) {

        JobDescriptionResponse response = jobDescriptionService.update(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Job updated", response));
    }

    /**
     * Permanently deletes a job description.
     *
     * @param id the job ID
     * @param authentication the authenticated HR user
     * @return the deletion response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            Authentication authentication) {

        jobDescriptionService.delete(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Job deleted", null));
    }

    /**
     * Returns a single job description by ID.
     *
     * @param id the job ID
     * @return the job description response
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDescriptionResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Job fetched", jobDescriptionService.getById(id)));
    }

    /**
     * Returns all job descriptions for the HR management view.
     *
     * @return the list of all job descriptions
     */
    @GetMapping("/hr")
    public ResponseEntity<ApiResponse<List<JobDescriptionResponse>>> getAllForHR() {
        return ResponseEntity.ok(
                ApiResponse.success("All jobs (HR view)", jobDescriptionService.findAllForHR()));
    }

    /**
     * Toggles the active or inactive state of a job.
     *
     * @param id the job ID
     * @param authentication the authenticated HR user
     * @return the toggle response
     */
    @PutMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleJob(
            @PathVariable Long id,
            Authentication authentication) {

        jobDescriptionService.toggleActive(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Job status updated", null));
    }
}
