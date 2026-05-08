package com.Capstone.InterviewTracking.service.impl;

import com.Capstone.InterviewTracking.dto.JobDescriptionRequest;
import com.Capstone.InterviewTracking.dto.JobDescriptionResponse;
import com.Capstone.InterviewTracking.entity.JobDescription;
import com.Capstone.InterviewTracking.entity.User;
import com.Capstone.InterviewTracking.enums.ApplicationStatus;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import com.Capstone.InterviewTracking.exception.UserNotFoundException;
import com.Capstone.InterviewTracking.mapper.JobDescriptionMapper;
import com.Capstone.InterviewTracking.repository.ApplicationRepository;
import com.Capstone.InterviewTracking.repository.JobDescriptionRepository;
import com.Capstone.InterviewTracking.repository.UserRepository;
import com.Capstone.InterviewTracking.service.JobDescriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of JobDescriptionService that manages job postings.
 */
@Service
public class JobDescriptionServiceImpl implements JobDescriptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobDescriptionServiceImpl.class);

    private final JobDescriptionRepository jobDescriptionRepository;
    private final UserRepository userRepository;
    private final JobDescriptionMapper jobDescriptionMapper;
    private final ApplicationRepository applicationRepository;

    /**
     * Creates a JobDescriptionServiceImpl with the required dependencies.
     *
     * @param jobDescriptionRepository the job description repository
     * @param userRepository the user repository
     * @param jobDescriptionMapper the job description mapper
     * @param applicationRepository the application repository
     */
    public JobDescriptionServiceImpl(JobDescriptionRepository jobDescriptionRepository,
                                     UserRepository userRepository,
                                     JobDescriptionMapper jobDescriptionMapper,
                                     ApplicationRepository applicationRepository) {
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.userRepository = userRepository;
        this.jobDescriptionMapper = jobDescriptionMapper;
        this.applicationRepository = applicationRepository;
    }

    /**
     * Creates a new job description.
     *
     * @param request the job details
     * @param createdByEmail the HR user's email
     * @return the created job description response
     */
    @Override
    public JobDescriptionResponse create(JobDescriptionRequest request, String createdByEmail) {
        validateRange(request.getMinExperience(), request.getMaxExperience(), "Experience");
        validateRange(request.getMinSalary(), request.getMaxSalary(), "Salary");

        User createdBy = userRepository.findByEmail(createdByEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        JobDescription savedJob = jobDescriptionRepository.save(
                jobDescriptionMapper.toEntity(request, createdBy)
        );

        LOGGER.info("Created job description with id: {}", savedJob.getId());
        return jobDescriptionMapper.toResponse(savedJob);
    }

    /**
     * Returns all active job descriptions for the public listing.
     *
     * @return list of active job descriptions
     */
    @Override
    public List<JobDescriptionResponse> findActiveJobs() {
        return jobDescriptionRepository.findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(jobDescriptionMapper::toResponse)
                .toList();
    }

    /**
     * Validates that the min value does not exceed the max value.
     *
     * @param min the minimum value
     * @param max the maximum value
     * @param fieldName the field name used in the error message
     */
    private void validateRange(Integer min, Integer max, String fieldName) {
        if (min != null && max != null && min > max) {
            throw new BadRequestException(fieldName + " minimum cannot be greater than maximum");
        }
    }

    /**
     * Validates that the min value does not exceed the max value.
     *
     * @param min the minimum value
     * @param max the maximum value
     * @param fieldName the field name used in the error message
     */
    private void validateRange(Double min, Double max, String fieldName) {
        if (min != null && max != null && min > max) {
            throw new BadRequestException(fieldName + " minimum cannot be greater than maximum");
        }
    }

    /**
     * Updates an existing job description.
     *
     * @param id the job ID
     * @param request the updated job details
     * @param email the HR user's email
     * @return the updated job description response
     */
    @Override
    public JobDescriptionResponse update(Long id, JobDescriptionRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getRole().name().equals("HR")) {
            throw new BadRequestException("Only HR can update job descriptions");
        }

        JobDescription jd = jobDescriptionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Job not found"));

        validateRange(request.getMinExperience(), request.getMaxExperience(), "Experience");
        validateRange(request.getMinSalary(), request.getMaxSalary(), "Salary");

        jd.setTitle(request.getTitle());
        jd.setDescription(request.getDescription());
        jd.setSkills(request.getSkills());
        jd.setLocation(request.getLocation());
        jd.setMinSalary(request.getMinSalary());
        jd.setMaxSalary(request.getMaxSalary());
        jd.setMinExperience(request.getMinExperience());
        jd.setMaxExperience(request.getMaxExperience());
        jd.setJobType(request.getJobType());

        LOGGER.info("JD updated with id: {}", id);

        return jobDescriptionMapper.toResponse(jobDescriptionRepository.save(jd));
    }

    /**
     * Permanently deletes a job description if no active applications exist.
     *
     * @param id the job ID
     * @param email the HR user's email
     */
    @Override
    public void delete(Long id, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getRole().name().equals("HR")) {
            throw new BadRequestException("Only HR can delete job");
        }

        JobDescription jd = jobDescriptionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Job not found"));

        boolean hasActiveApplications = applicationRepository
                .existsByJobAndStatusNot(jd, ApplicationStatus.REJECTED);

        if (hasActiveApplications) {
            throw new BadRequestException(
                    "Cannot delete this job — candidates are still active. Deactivate the job instead.");
        }

        jobDescriptionRepository.delete(jd);
        LOGGER.info("Job permanently deleted with id: {}", id);
    }

    /**
     * Returns a job description by ID with the hasApplications flag populated.
     *
     * @param id the job ID
     * @return the job description response
     */
    @Override
    public JobDescriptionResponse getById(Long id) {

        JobDescription jd = jobDescriptionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Job not found"));

        LOGGER.info("HR fetching job by id");
        JobDescriptionResponse r = jobDescriptionMapper.toResponse(jd);
        r.setHasApplications(applicationRepository.existsByJob(jd));
        return r;
    }

    /**
     * Returns all job descriptions for the HR management view.
     *
     * @return list of all job descriptions
     */
    @Override
    public List<JobDescriptionResponse> findAllForHR() {

        LOGGER.info("HR fetching all jobs");

        return jobDescriptionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(jd -> {
                    JobDescriptionResponse r = jobDescriptionMapper.toResponse(jd);
                    r.setHasApplications(applicationRepository.existsByJob(jd));
                    return r;
                })
                .toList();
    }

    /**
     * Toggles the active or inactive state of a job.
     *
     * @param id the job ID
     * @param email the HR user's email
     */
    @Override
    public void toggleActive(Long id, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getRole().name().equals("HR")) {
            throw new BadRequestException("Only HR can change job status");
        }

        JobDescription jd = jobDescriptionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Job not found"));

        jd.setActive(!jd.isActive());

        jobDescriptionRepository.save(jd);
        LOGGER.info("User {} toggling job id {}", email, id);
    }

}
