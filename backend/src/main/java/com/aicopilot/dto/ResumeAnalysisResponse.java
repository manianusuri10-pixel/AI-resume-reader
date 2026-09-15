package com.aicopilot.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResumeAnalysisResponse {
    private Long resumeId;
    private String fileName;
    private Integer atsScore;
    private Integer formattingScore;
    private Integer impactScore;
    private Integer totalWords;
    private Integer actionVerbsCount;
    private Integer quantifiableMetricsCount;
    private List<String> extractedSkills = new ArrayList<>();
    private Map<String, String> sections;
    private List<String> strengths = new ArrayList<>();
    private List<String> criticalImprovements = new ArrayList<>();
    private List<BulletRewriteDto> bulletRewrites = new ArrayList<>();
    private String rawText;
    private LocalDateTime createdAt;

    public ResumeAnalysisResponse() {}

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Integer getAtsScore() { return atsScore; }
    public void setAtsScore(Integer atsScore) { this.atsScore = atsScore; }

    public Integer getFormattingScore() { return formattingScore; }
    public void setFormattingScore(Integer formattingScore) { this.formattingScore = formattingScore; }

    public Integer getImpactScore() { return impactScore; }
    public void setImpactScore(Integer impactScore) { this.impactScore = impactScore; }

    public Integer getTotalWords() { return totalWords; }
    public void setTotalWords(Integer totalWords) { this.totalWords = totalWords; }

    public Integer getActionVerbsCount() { return actionVerbsCount; }
    public void setActionVerbsCount(Integer actionVerbsCount) { this.actionVerbsCount = actionVerbsCount; }

    public Integer getQuantifiableMetricsCount() { return quantifiableMetricsCount; }
    public void setQuantifiableMetricsCount(Integer quantifiableMetricsCount) { this.quantifiableMetricsCount = quantifiableMetricsCount; }

    public List<String> getExtractedSkills() { return extractedSkills; }
    public void setExtractedSkills(List<String> extractedSkills) { this.extractedSkills = extractedSkills; }

    public Map<String, String> getSections() { return sections; }
    public void setSections(Map<String, String> sections) { this.sections = sections; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getCriticalImprovements() { return criticalImprovements; }
    public void setCriticalImprovements(List<String> criticalImprovements) { this.criticalImprovements = criticalImprovements; }

    public List<BulletRewriteDto> getBulletRewrites() { return bulletRewrites; }
    public void setBulletRewrites(List<BulletRewriteDto> bulletRewrites) { this.bulletRewrites = bulletRewrites; }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
