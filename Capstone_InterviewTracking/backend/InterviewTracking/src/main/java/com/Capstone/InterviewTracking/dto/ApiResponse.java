package com.Capstone.InterviewTracking.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Generic wrapper for all API responses returned by the application.
 *
 * @param <T> the type of the data payload
 */
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private LocalDateTime timestamp;

    /**
     * Creates an ApiResponse with all fields.
     *
     * @param success whether the operation succeeded
     * @param message a human-readable description of the result
     * @param data the response payload
     * @param errors a list of validation or error messages
     */
    public ApiResponse(boolean success, String message, T data, List<String> errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Creates a successful response with data.
     *
     * @param message the success message
     * @param data the response payload
     * @param <T> the payload type
     * @return the success response
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, List.of());
    }

    /**
     * Creates a failure response with a single error message.
     *
     * @param message the error description
     * @param <T> the payload type
     * @return the failure response
     */
    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(false, message, null, List.of());
    }

    /**
     * Creates a failure response with a message and a list of error details.
     *
     * @param message the error description
     * @param errors list of validation or error messages
     * @param <T> the payload type
     * @return the failure response
     */
    public static <T> ApiResponse<T> failure(String message, List<String> errors) {
        return new ApiResponse<>(false, message, null, errors);
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
