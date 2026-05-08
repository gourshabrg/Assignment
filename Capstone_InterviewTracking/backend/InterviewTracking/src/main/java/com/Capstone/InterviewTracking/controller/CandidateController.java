package com.Capstone.InterviewTracking.controller;

import com.Capstone.InterviewTracking.constant.AppConstants;
import com.Capstone.InterviewTracking.dto.ApiResponse;
import com.Capstone.InterviewTracking.dto.ApplicationResponse;
import com.Capstone.InterviewTracking.dto.CandidateDetailResponse;
import com.Capstone.InterviewTracking.service.CandidateService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for candidate job application submission and status retrieval.
 */
@RestController
@RequestMapping
public class CandidateController {

    private final CandidateService service;

    /**
     * Creates a CandidateController with the required candidate service.
     *
     * @param service the candidate service
     */
    public CandidateController(CandidateService service) {
        this.service = service;
    }

    /**
     * Submits a job application from a candidate.
     *
     * @param fullName the candidate's full name
     * @param email the candidate's email address
     * @param mobile the candidate's mobile number
     * @param dob the candidate's date of birth
     * @param resume the PDF resume file
     * @param currentCompany the candidate's current employer
     * @param totalExperience total years of experience
     * @param relevantExperience years of relevant experience
     * @param currentCtc current annual CTC
     * @param expectedCtc expected annual CTC
     * @param noticePeriod notice period in days
     * @param preferredLocation preferred work location
     * @param source recruitment channel or referral source
     * @param jobId the ID of the job being applied for
     * @return the application response
     */
    @PostMapping(value = AppConstants.CANDIDATE_JOB_APPLY, consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String mobile,
            @RequestParam(required = false) String dob,
            @RequestParam MultipartFile resume,
            @RequestParam(required = false) String currentCompany,
            @RequestParam(required = false) Double totalExperience,
            @RequestParam(required = false) Double relevantExperience,
            @RequestParam(required = false) Double currentCtc,
            @RequestParam(required = false) Double expectedCtc,
            @RequestParam(required = false) Integer noticePeriod,
            @RequestParam(required = false) String preferredLocation,
            @RequestParam(required = false) String source,
            @RequestParam Long jobId
    ) {
        ApplicationResponse response = service.applyCandidate(
                fullName, email, mobile, dob, resume,
                currentCompany, totalExperience, relevantExperience,
                currentCtc, expectedCtc, noticePeriod,
                preferredLocation, source, jobId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Application submitted", response));
    }

    /**
     * Creates a candidate profile and application on behalf of an HR user.
     *
     * @param fullName the candidate's full name
     * @param email the candidate's email address
     * @param mobile the candidate's mobile number
     * @param dob the candidate's date of birth
     * @param resume the PDF resume file
     * @param currentCompany the candidate's current employer
     * @param totalExperience total years of experience
     * @param relevantExperience years of relevant experience
     * @param currentCtc current annual CTC
     * @param expectedCtc expected annual CTC
     * @param noticePeriod notice period in days
     * @param preferredLocation preferred work location
     * @param source recruitment channel or referral source
     * @param jobId the ID of the job
     * @param authentication the authenticated HR user
     * @return the application response
     */
    @PostMapping(value = AppConstants.HR_CREATE_CANDIDATE, consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ApplicationResponse>> createByHR(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String mobile,
            @RequestParam(required = false) String dob,
            @RequestParam MultipartFile resume,
            @RequestParam(required = false) String currentCompany,
            @RequestParam(required = false) Double totalExperience,
            @RequestParam(required = false) Double relevantExperience,
            @RequestParam(required = false) Double currentCtc,
            @RequestParam(required = false) Double expectedCtc,
            @RequestParam(required = false) Integer noticePeriod,
            @RequestParam(required = false) String preferredLocation,
            @RequestParam(required = false) String source,
            @RequestParam Long jobId,
            Authentication authentication
    ) {
        ApplicationResponse response = service.createByHR(
                fullName, email, mobile, dob, resume,
                currentCompany, totalExperience, relevantExperience,
                currentCtc, expectedCtc, noticePeriod,
                preferredLocation, source, jobId,
                authentication.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Candidate created", response));
    }

    /**
     * Returns the application details for the currently authenticated candidate.
     *
     * @param authentication the authenticated candidate
     * @return the candidate's application details
     */
    @GetMapping(AppConstants.CANDIDATE_MY_APPLICATION)
    public ResponseEntity<ApiResponse<CandidateDetailResponse>> getMyApplication(Authentication authentication) {
        CandidateDetailResponse response = service.getMyApplication(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Application fetched", response));
    }
}
