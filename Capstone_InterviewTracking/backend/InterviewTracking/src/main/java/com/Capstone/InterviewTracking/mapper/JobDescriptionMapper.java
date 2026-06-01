package com.Capstone.InterviewTracking.mapper;

import com.Capstone.InterviewTracking.dto.JobDescriptionRequest;
import com.Capstone.InterviewTracking.dto.JobDescriptionResponse;
import com.Capstone.InterviewTracking.entity.JobDescription;
import com.Capstone.InterviewTracking.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper component for converting between JobDescription entities and DTOs.
 */
@Component
public class JobDescriptionMapper {

    /**
     * Converts a JobDescriptionRequest to a JobDescription entity.
     *
     * @param request the job description request
     * @param createdBy the HR user creating the job
     * @return the job description entity
     */
    public JobDescription toEntity(JobDescriptionRequest request, User createdBy) {
        JobDescription jobDescription = new JobDescription();
        jobDescription.setTitle(request.getTitle().trim());
        jobDescription.setDescription(request.getDescription().trim());
        jobDescription.setSkills(request.getSkills().trim());
        jobDescription.setLocation(request.getLocation().trim());
        jobDescription.setMinSalary(request.getMinSalary());
        jobDescription.setMaxSalary(request.getMaxSalary());
        jobDescription.setMinExperience(request.getMinExperience());
        jobDescription.setMaxExperience(request.getMaxExperience());
        jobDescription.setJobType(request.getJobType());
        jobDescription.setActive(true);
        jobDescription.setCreatedBy(createdBy);
        return jobDescription;
    }

    /**
     * Converts a JobDescription entity to a JobDescriptionResponse DTO.
     *
     * @param jobDescription the job description entity
     * @return the job description response
     */
    public JobDescriptionResponse toResponse(JobDescription jobDescription) {
        JobDescriptionResponse response = new JobDescriptionResponse();
        response.setId(jobDescription.getId());
        response.setTitle(jobDescription.getTitle());
        response.setDescription(jobDescription.getDescription());
        response.setSkills(jobDescription.getSkills());
        response.setLocation(jobDescription.getLocation());
        response.setMinSalary(jobDescription.getMinSalary());
        response.setMaxSalary(jobDescription.getMaxSalary());
        response.setMinExperience(jobDescription.getMinExperience());
        response.setMaxExperience(jobDescription.getMaxExperience());
        response.setJobType(jobDescription.getJobType());
        response.setActive(jobDescription.isActive());
        response.setCreatedAt(jobDescription.getCreatedAt());
        return response;
    }
}
