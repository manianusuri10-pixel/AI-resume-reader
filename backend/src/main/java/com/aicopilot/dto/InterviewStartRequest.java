package com.aicopilot.dto;

import jakarta.validation.constraints.NotBlank;

public class InterviewStartRequest {
    @NotBlank(message = "Target role is required")
    private String targetRole;

    private String difficulty = "MID"; // ENTRY, MID, SENIOR, LEAD

    private Integer questionCount = 3;

    public InterviewStartRequest() {}

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
}
