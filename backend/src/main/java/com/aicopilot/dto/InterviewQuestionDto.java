package com.aicopilot.dto;

import com.aicopilot.model.InterviewQuestion;
import java.time.LocalDateTime;

public class InterviewQuestionDto {
    private Long id;
    private Integer questionNumber;
    private String questionText;
    private String category;
    private String sampleIdealAnswer;
    private String userResponse;
    private Double aiScore;
    private String aiCritique;
    private String suggestedImprovement;
    private LocalDateTime evaluatedAt;

    public InterviewQuestionDto() {}

    public InterviewQuestionDto(InterviewQuestion q) {
        this.id = q.getId();
        this.questionNumber = q.getQuestionNumber();
        this.questionText = q.getQuestionText();
        this.category = q.getCategory();
        this.sampleIdealAnswer = q.getSampleIdealAnswer();
        this.userResponse = q.getUserResponse();
        this.aiScore = q.getAiScore();
        this.aiCritique = q.getAiCritique();
        this.suggestedImprovement = q.getSuggestedImprovement();
        this.evaluatedAt = q.getEvaluatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getQuestionNumber() { return questionNumber; }
    public void setQuestionNumber(Integer questionNumber) { this.questionNumber = questionNumber; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSampleIdealAnswer() { return sampleIdealAnswer; }
    public void setSampleIdealAnswer(String sampleIdealAnswer) { this.sampleIdealAnswer = sampleIdealAnswer; }

    public String getUserResponse() { return userResponse; }
    public void setUserResponse(String userResponse) { this.userResponse = userResponse; }

    public Double getAiScore() { return aiScore; }
    public void setAiScore(Double aiScore) { this.aiScore = aiScore; }

    public String getAiCritique() { return aiCritique; }
    public void setAiCritique(String aiCritique) { this.aiCritique = aiCritique; }

    public String getSuggestedImprovement() { return suggestedImprovement; }
    public void setSuggestedImprovement(String suggestedImprovement) { this.suggestedImprovement = suggestedImprovement; }

    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}
