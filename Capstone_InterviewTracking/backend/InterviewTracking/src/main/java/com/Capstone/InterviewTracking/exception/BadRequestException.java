package com.Capstone.InterviewTracking.exception;

/**
 * Thrown when a request violates business rules or contains invalid data.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Creates a new BadRequestException with the given message.
     *
     * @param message description of the error
     */
    public BadRequestException(String message) {
        super(message);
    }
}
