package com.Capstone.InterviewTracking.entity;

import com.Capstone.InterviewTracking.enums.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "interview")
public class Interview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @Enumerated(EnumType.STRING)
    private InterviewRound round;

    private LocalDateTime interviewDateTime;

    private String focusArea;

    @Enumerated(EnumType.STRING)
    private InterviewStatus status;

    @ManyToMany
    @JoinTable(
        name = "interview_panel",
        joinColumns = @JoinColumn(name = "interview_id"),
        inverseJoinColumns = @JoinColumn(name = "panel_id")
    )
    private List<Panel> panels;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }

    public InterviewRound getRound() { return round; }
    public void setRound(InterviewRound round) { this.round = round; }

    public LocalDateTime getInterviewDateTime() { return interviewDateTime; }
    public void setInterviewDateTime(LocalDateTime interviewDateTime) { this.interviewDateTime = interviewDateTime; }

    public String getFocusArea() { return focusArea; }
    public void setFocusArea(String focusArea) { this.focusArea = focusArea; }

    public InterviewStatus getStatus() { return status; }
    public void setStatus(InterviewStatus status) { this.status = status; }

    public List<Panel> getPanels() { return panels; }
    public void setPanels(List<Panel> panels) { this.panels = panels; }
}