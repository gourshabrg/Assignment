package com.Capstone.InterviewTracking.dto;

/**
 * Response DTO representing submitted feedback for an interview round.
 */
public class FeedbackResponse {

    private Long id;
    private Long interviewId;
    private String round;

    /** Full name of the panel member, or HR reviewer email for the HR round. */
    private String panelName;

    private String comments;
    private String strengths;
    private String weaknesses;
    private String areasCovered;
    private int rating;

    /** Final recommendation: SELECTED or REJECTED. */
    private String status;

    public FeedbackResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getInterviewId() { return interviewId; }
    public void setInterviewId(Long interviewId) { this.interviewId = interviewId; }

    public String getRound() { return round; }
    public void setRound(String round) { this.round = round; }

    public String getPanelName() { return panelName; }
    public void setPanelName(String panelName) { this.panelName = panelName; }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
