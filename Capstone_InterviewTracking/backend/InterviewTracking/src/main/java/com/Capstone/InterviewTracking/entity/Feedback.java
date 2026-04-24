package com.Capstone.InterviewTracking.entity;

import com.Capstone.InterviewTracking.enums.FeedbackStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "feedback")
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @ManyToOne
    @JoinColumn(name = "panel_id")
    private Panel panel;

    private String comments;
    private String strengths;
    private String weaknesses;

    private int rating;

    @Enumerated(EnumType.STRING)
    private FeedbackStatus status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Interview getInterview() { return interview; }
    public void setInterview(Interview interview) { this.interview = interview; }

    public Panel getPanel() { return panel; }
    public void setPanel(Panel panel) { this.panel = panel; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }

    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public FeedbackStatus getStatus() { return status; }
    public void setStatus(FeedbackStatus status) { this.status = status; }
}