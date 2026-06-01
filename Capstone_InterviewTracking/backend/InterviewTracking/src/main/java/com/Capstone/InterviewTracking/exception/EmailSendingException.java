package com.Capstone.InterviewTracking.exception;

/**
 * Thrown when the application fails to send an email.
 */
public class EmailSendingException extends RuntimeException {

    /**
     * Creates a new EmailSendingException with the given message.
     *
     * @param message description of the failure
     */
    public EmailSendingException(String message) {
        super(message);
    }
}
