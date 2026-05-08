package com.Capstone.InterviewTracking.service;

/**
 * Service interface for sending transactional emails.
 */
public interface EmailService {

    /**
     * Sends an account verification email with a link to set the password.
     *
     * @param toEmail the recipient's email
     * @param name the recipient's name
     * @param token the verification token
     */
    void sendVerificationMail(String toEmail, String name, String token);

    /**
     * Sends an interview scheduling notification to the candidate.
     *
     * @param toEmail the candidate's email
     * @param candidateName the candidate's name
     * @param round the interview round
     * @param dateTime the scheduled date and time
     * @param panelNames names of the assigned panel members
     */
    void sendInterviewScheduledMail(String toEmail, String candidateName,
                                    String round, String dateTime, String panelNames);

    /**
     * Sends an interview scheduling notification to the HR user.
     *
     * @param hrEmail the HR user's email
     * @param candidateName the candidate's name
     * @param candidateEmail the candidate's email
     * @param dateTime the scheduled date and time
     */
    void sendHRInterviewScheduledMail(String hrEmail, String candidateName,
                                      String candidateEmail, String dateTime);
}
