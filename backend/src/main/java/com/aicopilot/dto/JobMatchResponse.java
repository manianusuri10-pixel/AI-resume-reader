package com.aicopilot.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobMatchResponse {
    private Long matchId;
    private String targetRole;
    private String targetCompany;
    private Integer matchScore;
    private Integer skillMatchPercentage;
    private List<String> matchedSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private List<String> recommendations = new ArrayList<>();
    private String tailoredPitch;
    private LocalDateTime createdAt;

    public JobMatchResponse() {}

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getTargetCompany() { return targetCompany; }
    public void setTargetCompany(String targetCompany) { this.targetCompany = targetCompany; }

    public Integer getMatchScore() { return matchScore; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }

    public Integer getSkillMatchPercentage() { return skillMatchPercentage; }
    public void setSkillMatchPercentage(Integer skillMatchPercentage) { this.skillMatchPercentage = skillMatchPercentage; }

    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }

    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }

    public String getTailoredPitch() { return tailoredPitch; }
    public void setTailoredPitch(String tailoredPitch) { this.tailoredPitch = tailoredPitch; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
