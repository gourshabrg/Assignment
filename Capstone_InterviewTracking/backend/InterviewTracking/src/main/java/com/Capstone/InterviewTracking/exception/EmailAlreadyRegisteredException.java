package com.Capstone.InterviewTracking.exception;

/**
 * Thrown when a registration attempt uses an email that is already registered.
 */
public class EmailAlreadyRegisteredException extends RuntimeException {

    /**
     * Creates a new EmailAlreadyRegisteredException with the given message.
     *
     * @param message description of the conflict
     */
    public EmailAlreadyRegisteredException(String message) {
        super(message);
    }
}
