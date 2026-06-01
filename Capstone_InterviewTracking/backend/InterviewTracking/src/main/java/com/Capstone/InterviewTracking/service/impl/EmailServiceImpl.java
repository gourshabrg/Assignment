package com.Capstone.InterviewTracking.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.Capstone.InterviewTracking.constant.AppConstants;
import com.Capstone.InterviewTracking.exception.EmailSendingException;
import com.Capstone.InterviewTracking.service.EmailService;

import jakarta.mail.internet.MimeMessage;

/**
 * Implementation of EmailService that sends HTML emails via JavaMail.
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends an account verification email with a link to set the password.
     *
     * @param email the recipient's email
     * @param name the recipient's name
     * @param token the verification token
     */
    @Override
    public void sendVerificationMail(String email, String name, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(AppConstants.EMAIL_SUBJECT);
            helper.setText(AppConstants.buildEmailMessage(name, token), true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EmailSendingException("Failed to send verification email");
        }
    }

    /**
     * Sends an interview scheduling notification to the candidate.
     *
     * @param toEmail the candidate's email
     * @param candidateName the candidate's name
     * @param round the interview round
     * @param dateTime the scheduled date and time
     * @param panelNames the assigned panel members' names
     */
    @Override
    public void sendInterviewScheduledMail(String toEmail, String candidateName,
                                           String round, String dateTime, String panelNames) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Interview Scheduled – " + round + " Round");
            helper.setText(AppConstants.buildInterviewScheduledMessage(candidateName, round, dateTime, panelNames), true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EmailSendingException("Failed to send interview scheduled email");
        }
    }

    /**
     * Sends an HR-round interview scheduling notification to the HR user.
     *
     * @param hrEmail the HR user's email
     * @param candidateName the candidate's name
     * @param candidateEmail the candidate's email
     * @param dateTime the scheduled date and time
     */
    @Override
    public void sendHRInterviewScheduledMail(String hrEmail, String candidateName,
                                             String candidateEmail, String dateTime) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(hrEmail);
            helper.setSubject("HR Round Scheduled – " + candidateName);
            helper.setText(AppConstants.buildHRInterviewScheduledMessage(hrEmail, candidateName, candidateEmail, dateTime), true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EmailSendingException("Failed to send HR interview scheduled email");
        }
    }
}
