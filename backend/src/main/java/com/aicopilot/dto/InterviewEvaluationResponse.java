package com.aicopilot.dto;

public class InterviewEvaluationResponse {
    private Double score; // 1 to 10
    private String critique;
    private String strengths;
    private String missingConcepts;
    private String suggestedImprovement;
    private String modelIdealAnswer;

    public InterviewEvaluationResponse() {}

    public InterviewEvaluationResponse(Double score, String critique, String strengths, String missingConcepts, String suggestedImprovement, String modelIdealAnswer) {
        this.score = score;
        this.critique = critique;
        this.strengths = strengths;
        this.missingConcepts = missingConcepts;
        this.suggestedImprovement = suggestedImprovement;
        this.modelIdealAnswer = modelIdealAnswer;
    }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getCritique() { return critique; }
    public void setCritique(String critique) { this.critique = critique; }

    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }

    public String getMissingConcepts() { return missingConcepts; }
    public void setMissingConcepts(String missingConcepts) { this.missingConcepts = missingConcepts; }

    public String getSuggestedImprovement() { return suggestedImprovement; }
    public void setSuggestedImprovement(String suggestedImprovement) { this.suggestedImprovement = suggestedImprovement; }

    public String getModelIdealAnswer() { return modelIdealAnswer; }
    public void setModelIdealAnswer(String modelIdealAnswer) { this.modelIdealAnswer = modelIdealAnswer; }
}
