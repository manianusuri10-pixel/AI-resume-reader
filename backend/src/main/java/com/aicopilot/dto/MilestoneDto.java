package com.aicopilot.dto;

import java.util.ArrayList;
import java.util.List;

public class MilestoneDto {
    private Integer phaseNumber;
    private String title;
    private Integer durationWeeks;
    private String description;
    private List<String> keyTopics = new ArrayList<>();
    private List<String> actionItems = new ArrayList<>();
    private List<String> recommendedResources = new ArrayList<>();

    public MilestoneDto() {}

    public MilestoneDto(Integer phaseNumber, String title, Integer durationWeeks, String description, List<String> keyTopics, List<String> actionItems, List<String> recommendedResources) {
        this.phaseNumber = phaseNumber;
        this.title = title;
        this.durationWeeks = durationWeeks;
        this.description = description;
        this.keyTopics = keyTopics;
        this.actionItems = actionItems;
        this.recommendedResources = recommendedResources;
    }

    public Integer getPhaseNumber() { return phaseNumber; }
    public void setPhaseNumber(Integer phaseNumber) { this.phaseNumber = phaseNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getDurationWeeks() { return durationWeeks; }
    public void setDurationWeeks(Integer durationWeeks) { this.durationWeeks = durationWeeks; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getKeyTopics() { return keyTopics; }
    public void setKeyTopics(List<String> keyTopics) { this.keyTopics = keyTopics; }

    public List<String> getActionItems() { return actionItems; }
    public void setActionItems(List<String> actionItems) { this.actionItems = actionItems; }

    public List<String> getRecommendedResources() { return recommendedResources; }
    public void setRecommendedResources(List<String> recommendedResources) { this.recommendedResources = recommendedResources; }
}
