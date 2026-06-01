package com.Capstone.InterviewTracking.dto;

/**
 * Request DTO for advancing a candidate's hiring stage.
 */
public class StageUpdateRequest {

    /** The target stage name. */
    private String stage;

    /** Optional HR comments or notes about the stage transition. */
    private String comments;

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
