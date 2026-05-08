package com.Capstone.InterviewTracking.service;

import org.springframework.web.multipart.MultipartFile;

import com.Capstone.InterviewTracking.dto.ApplicationResponse;
import com.Capstone.InterviewTracking.dto.CandidateDetailResponse;

/**
 * Service interface for candidate application operations.
 */
public interface CandidateService {

    /**
     * Returns the application and interview history for the authenticated candidate.
     *
     * @param email the candidate's email
     * @return the candidate detail response
     */
    CandidateDetailResponse getMyApplication(String email);

    /**
     * Submits a job application from a candidate.
     *
     * @param fullName the candidate's full name
     * @param email the candidate's email
     * @param mobile the candidate's mobile number
     * @param dob the candidate's date of birth
     * @param resume the PDF resume file
     * @param currentCompany the current employer
     * @param totalExp total years of experience
     * @param relExp relevant years of experience
     * @param currentCtc current CTC in rupees
     * @param expectedCtc expected CTC in rupees
     * @param noticePeriod notice period in days
     * @param location preferred work location
     * @param source recruitment source
     * @param jobId the job ID being applied for
     * @return the application response
     */
    ApplicationResponse applyCandidate(
            String fullName, String email, String mobile, String dob,
            MultipartFile resume,
            String currentCompany, Double totalExp, Double relExp,
            Double currentCtc, Double expectedCtc,
            Integer noticePeriod, String location, String source,
            Long jobId
    );

    /**
     * Creates a candidate profile and application on behalf of an HR user.
     *
     * @param fullName the candidate's full name
     * @param email the candidate's email
     * @param mobile the candidate's mobile number
     * @param dob the candidate's date of birth
     * @param resume the PDF resume file
     * @param currentCompany the current employer
     * @param totalExp total years of experience
     * @param relExp relevant years of experience
     * @param currentCtc current CTC in rupees
     * @param expectedCtc expected CTC in rupees
     * @param noticePeriod notice period in days
     * @param location preferred work location
     * @param source recruitment source
     * @param jobId the job ID
     * @param hrEmail the HR user's email
     * @return the application response
     */
    ApplicationResponse createByHR(
            String fullName, String email, String mobile, String dob,
            MultipartFile resume,
            String currentCompany, Double totalExp, Double relExp,
            Double currentCtc, Double expectedCtc,
            Integer noticePeriod, String location, String source,
            Long jobId,
            String hrEmail
    );
}
