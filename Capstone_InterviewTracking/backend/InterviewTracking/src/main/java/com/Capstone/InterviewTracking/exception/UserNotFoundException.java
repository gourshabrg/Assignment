package com.Capstone.InterviewTracking.exception;

/**
 * Thrown when a user account cannot be found in the database.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Creates a new UserNotFoundException with the given message.
     *
     * @param message description of the lookup failure
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
