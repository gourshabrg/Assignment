package com.Capstone.InterviewTracking.service.impl;

import com.Capstone.InterviewTracking.dto.ApplicationResponse;
import com.Capstone.InterviewTracking.dto.CandidateDetailResponse;
import com.Capstone.InterviewTracking.dto.InterviewResponse;
import com.Capstone.InterviewTracking.dto.PanelResponse;
import com.Capstone.InterviewTracking.dto.SignupRequest;
import com.Capstone.InterviewTracking.entity.Application;
import com.Capstone.InterviewTracking.entity.Candidate;
import com.Capstone.InterviewTracking.entity.Interview;
import com.Capstone.InterviewTracking.entity.JobDescription;
import com.Capstone.InterviewTracking.entity.User;
import com.Capstone.InterviewTracking.enums.ApplicationStatus;
import com.Capstone.InterviewTracking.enums.InterviewStage;
import com.Capstone.InterviewTracking.repository.ApplicationRepository;
import com.Capstone.InterviewTracking.repository.CandidateRepository;
import com.Capstone.InterviewTracking.repository.InterviewRepository;
import com.Capstone.InterviewTracking.repository.JobDescriptionRepository;
import com.Capstone.InterviewTracking.repository.UserRepository;
import com.Capstone.InterviewTracking.service.AuthService;
import com.Capstone.InterviewTracking.service.CandidateService;
import com.Capstone.InterviewTracking.service.DriveService;
import com.Capstone.InterviewTracking.util.FileValidationUtil;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CandidateService that handles job application submission and retrieval.
 */
@Service
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final JobDescriptionRepository jobRepository;
    private final DriveService driveService;
    private final AuthService authService;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;

    /**
     * Creates a CandidateServiceImpl with all required dependencies.
     *
     * @param candidateRepository the candidate repository
     * @param jobRepository the job description repository
     * @param driveService the Google Drive upload service
     * @param authService the authentication service
     * @param applicationRepository the application repository
     * @param userRepository the user repository
     * @param interviewRepository the interview repository
     */
    public CandidateServiceImpl(
            CandidateRepository candidateRepository,
            JobDescriptionRepository jobRepository,
            DriveService driveService,
            AuthService authService,
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            InterviewRepository interviewRepository) {

        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.driveService = driveService;
        this.authService = authService;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
    }

    /**
     * Returns the application and interview history for the authenticated candidate.
     *
     * @param email the candidate's email
     * @return the candidate detail response
     */
    @Override
    public CandidateDetailResponse getMyApplication(String email) {
        Candidate candidate = candidateRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Candidate profile not found"));

        Application application = applicationRepository.findByCandidate(candidate)
                .orElseThrow(() -> new BadRequestException("No application found"));

        JobDescription jd = application.getJob();

        CandidateDetailResponse candidateDetailResponse = new CandidateDetailResponse();
        candidateDetailResponse.setApplicationId(application.getId());
        candidateDetailResponse.setCandidateId(candidate.getId());
        candidateDetailResponse.setFullName(candidate.getFullName());
        candidateDetailResponse.setEmail(candidate.getEmail());
        candidateDetailResponse.setMobile(candidate.getMobile());
        candidateDetailResponse.setCurrentCompany(candidate.getCurrentCompany());
        candidateDetailResponse.setTotalExperience(candidate.getTotalExperience());
        candidateDetailResponse.setRelevantExperience(candidate.getRelevantExperience());
        candidateDetailResponse.setCurrentCtc(candidate.getCurrentCtc());
        candidateDetailResponse.setExpectedCtc(candidate.getExpectedCtc());
        candidateDetailResponse.setNoticePeriod(candidate.getNoticePeriod());
        candidateDetailResponse.setPreferredLocation(candidate.getPreferredLocation());
        candidateDetailResponse.setSource(candidate.getSource());
        candidateDetailResponse.setResumeUrl(candidate.getResumeUrl());
        candidateDetailResponse.setJobId(jd.getId());
        candidateDetailResponse.setJobTitle(jd.getTitle());
        candidateDetailResponse.setJobDescription(jd.getDescription());
        candidateDetailResponse.setSkills(jd.getSkills());
        candidateDetailResponse.setLocation(jd.getLocation());
        candidateDetailResponse.setJobType(jd.getJobType() != null ? jd.getJobType().name() : null);
        candidateDetailResponse.setStage(application.getStage().name());
        candidateDetailResponse.setStatus(application.getStatus().name());
        candidateDetailResponse.setAppliedAt(application.getCreatedAt());

        List<Interview> interviews = interviewRepository.findByCandidate(candidate);
        candidateDetailResponse.setInterviews(interviews.stream().map(this::mapInterview).collect(Collectors.toList()));

        return candidateDetailResponse;
    }

    /**
     * Converts an Interview entity to an InterviewResponse DTO.
     *
     * @param interview the interview entity
     * @return the interview response
     */
    private InterviewResponse mapInterview(Interview interview) {
        InterviewResponse interviewResponse = new InterviewResponse();
        interviewResponse.setId(interview.getId());
        interviewResponse.setRound(interview.getRound().name());
        interviewResponse.setInterviewDateTime(interview.getInterviewDateTime());
        interviewResponse.setFocusArea(interview.getFocusArea());
        interviewResponse.setStatus(interview.getStatus().name());
        List<PanelResponse> panels = interview.getPanels().stream()
                .map(p -> new PanelResponse(p.getId(), p.getFullName(), p.getEmail(),
                        p.getPhone(), p.getOrganization(), p.getDesignation()))
                .collect(Collectors.toList());
        interviewResponse.setPanels(panels);
        return interviewResponse;
    }

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
     * @param currentCtc current CTC
     * @param expectedCtc expected CTC
     * @param noticePeriod notice period in days
     * @param location preferred location
     * @param source recruitment source
     * @param jobId the job ID
     * @return the application response
     */
    @Override
    public ApplicationResponse applyCandidate(
            String fullName, String email, String mobile, String dob,
            MultipartFile resume,
            String currentCompany, Double totalExp, Double relExp,
            Double currentCtc, Double expectedCtc,
            Integer noticePeriod, String location, String source,
            Long jobId) {

        FileValidationUtil.validatePdf(resume);

        Candidate candidate = candidateRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    Candidate c = new Candidate();
                    c.setEmail(email);
                    c.setUser(user);
                    c.setFullName(fullName);
                    c.setMobile(mobile);

                    return candidateRepository.save(c);
                });

        if (applicationRepository.existsByCandidateAndStatus(candidate, ApplicationStatus.APPLIED)) {
            throw new BadRequestException("You already have an active application");
        }

        JobDescription job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (applicationRepository.existsByCandidateAndJob(candidate, job)) {
            throw new BadRequestException("You already applied for this job");
        }

        String resumeUrl = driveService.uploadFile(resume);

        candidate.setFullName(fullName);
        candidate.setMobile(mobile);
        candidate.setCurrentCompany(currentCompany);
        candidate.setTotalExperience(totalExp);
        candidate.setRelevantExperience(relExp);
        candidate.setCurrentCtc(currentCtc);
        candidate.setExpectedCtc(expectedCtc);
        candidate.setNoticePeriod(noticePeriod);
        candidate.setPreferredLocation(location);
        candidate.setSource(source);
        candidate.setResumeUrl(resumeUrl);

        candidateRepository.save(candidate);

        Application app = new Application();
        app.setCandidate(candidate);
        app.setJob(job);
        app.setStage(InterviewStage.PROFILING);
        app.setStatus(ApplicationStatus.APPLIED);

        app = applicationRepository.save(app);

        return new ApplicationResponse(
                app.getId(),
                job.getId(),
                job.getTitle(),
                app.getStatus().name(),
                app.getStage().name(),
                candidate.getResumeUrl(),
                app.getCreatedAt()
        );
    }

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
     * @param currentCtc current CTC
     * @param expectedCtc expected CTC
     * @param noticePeriod notice period in days
     * @param location preferred location
     * @param source recruitment source
     * @param jobId the job ID
     * @param hrEmail the HR user's email
     * @return the application response
     */
    @Override
    public ApplicationResponse createByHR(
            String fullName, String email, String mobile, String dob,
            MultipartFile resume,
            String currentCompany, Double totalExp, Double relExp,
            Double currentCtc, Double expectedCtc,
            Integer noticePeriod, String location, String source,
            Long jobId,
            String hrEmail) {

        FileValidationUtil.validatePdf(resume);

        Candidate candidate = candidateRepository.findByEmail(email)
                .orElseGet(() -> {
                    Candidate c = new Candidate();
                    c.setEmail(email);
                    c.setFullName(fullName);
                    c.setMobile(mobile);
                    return candidateRepository.save(c);
                });

        if (applicationRepository.existsByCandidateAndStatus(candidate, ApplicationStatus.APPLIED)) {
            throw new BadRequestException("Candidate already has an active application");
        }

        JobDescription job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        String resumeUrl = driveService.uploadFile(resume);

        candidate.setFullName(fullName);
        candidate.setMobile(mobile);
        candidate.setCurrentCompany(currentCompany);
        candidate.setTotalExperience(totalExp);
        candidate.setRelevantExperience(relExp);
        candidate.setCurrentCtc(currentCtc);
        candidate.setExpectedCtc(expectedCtc);
        candidate.setNoticePeriod(noticePeriod);
        candidate.setPreferredLocation(location);
        candidate.setSource(source);
        candidate.setResumeUrl(resumeUrl);

        candidateRepository.save(candidate);

        Application app = new Application();
        app.setCandidate(candidate);
        app.setJob(job);
        app.setStage(InterviewStage.PROFILING);
        app.setStatus(ApplicationStatus.APPLIED);

        app = applicationRepository.save(app);

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(email);
        signupRequest.setFullName(fullName);

        authService.register(signupRequest);

        return new ApplicationResponse(
                app.getId(),
                job.getId(),
                job.getTitle(),
                app.getStatus().name(),
                app.getStage().name(),
                candidate.getResumeUrl(),
                app.getCreatedAt()
        );
    }
}
