package com.aicopilot.dto;

import com.aicopilot.model.InterviewSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InterviewSessionResponse {
    private Long id;
    private String targetRole;
    private String difficulty;
    private String status;
    private Double totalScore;
    private String feedbackSummary;
    private List<InterviewQuestionDto> questions = new ArrayList<>();
    private LocalDateTime createdAt;

    public InterviewSessionResponse() {}

    public InterviewSessionResponse(InterviewSession session) {
        this.id = session.getId();
        this.targetRole = session.getTargetRole();
        this.difficulty = session.getDifficulty();
        this.status = session.getStatus();
        this.totalScore = session.getTotalScore();
        this.feedbackSummary = session.getFeedbackSummary();
        if (session.getQuestions() != null) {
            this.questions = session.getQuestions().stream()
                    .map(InterviewQuestionDto::new)
                    .collect(Collectors.toList());
        }
        this.createdAt = session.getCreatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getTotalScore() { return totalScore; }
    public void setTotalScore(Double totalScore) { this.totalScore = totalScore; }

    public String getFeedbackSummary() { return feedbackSummary; }
    public void setFeedbackSummary(String feedbackSummary) { this.feedbackSummary = feedbackSummary; }

    public List<InterviewQuestionDto> getQuestions() { return questions; }
    public void setQuestions(List<InterviewQuestionDto> questions) { this.questions = questions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
