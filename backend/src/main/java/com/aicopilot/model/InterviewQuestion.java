package com.aicopilot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_questions")
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    @JsonBackReference
    private InterviewSession session;

    private Integer questionNumber;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String questionText;

    private String category;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String sampleIdealAnswer;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String userResponse;

    private Double aiScore; // 1 to 10

    @Lob
    @Column(columnDefinition = "CLOB")
    private String aiCritique;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String suggestedImprovement;

    private LocalDateTime evaluatedAt;

    public InterviewQuestion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InterviewSession getSession() { return session; }
    public void setSession(InterviewSession session) { this.session = session; }

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
