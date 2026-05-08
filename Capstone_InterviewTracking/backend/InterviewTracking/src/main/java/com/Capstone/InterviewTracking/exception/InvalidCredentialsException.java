package com.Capstone.InterviewTracking.exception;

/**
 * Thrown when a user provides invalid login credentials.
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Creates a new InvalidCredentialsException with the given message.
     *
     * @param message description of the authentication failure
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
