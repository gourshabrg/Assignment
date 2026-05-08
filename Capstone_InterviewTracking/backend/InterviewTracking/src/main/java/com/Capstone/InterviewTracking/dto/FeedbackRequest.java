package com.Capstone.InterviewTracking.dto;

import com.Capstone.InterviewTracking.enums.FeedbackStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for submitting post-interview feedback.
 */
public class FeedbackRequest {

    @NotBlank(message = "Comments are required")
    private String comments;

    /** Positive observations about the candidate's skills or attitude. */
    private String strengths;

    /** Areas where the candidate needs improvement or showed gaps. */
    private String weaknesses;

    @NotBlank(message = "Areas covered is required")
    private String areasCovered;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;

    @NotNull(message = "Status is required")
    private FeedbackStatus status;

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }

    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }

    public String getAreasCovered() { return areasCovered; }
    public void setAreasCovered(String areasCovered) { this.areasCovered = areasCovered; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public FeedbackStatus getStatus() { return status; }
    public void setStatus(FeedbackStatus status) { this.status = status; }
}
