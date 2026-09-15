package com.aicopilot.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoadmapResponse {
    private Long roadmapId;
    private String currentRole;
    private String targetRole;
    private Integer timelineMonths;
    private Integer estimatedWeeklyHours;
    private List<String> skillsToAcquire = new ArrayList<>();
    private List<MilestoneDto> milestones = new ArrayList<>();
    private LocalDateTime createdAt;

    public RoadmapResponse() {}

    public Long getRoadmapId() { return roadmapId; }
    public void setRoadmapId(Long roadmapId) { this.roadmapId = roadmapId; }

    public String getCurrentRole() { return currentRole; }
    public void setCurrentRole(String currentRole) { this.currentRole = currentRole; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public Integer getTimelineMonths() { return timelineMonths; }
    public void setTimelineMonths(Integer timelineMonths) { this.timelineMonths = timelineMonths; }

    public Integer getEstimatedWeeklyHours() { return estimatedWeeklyHours; }
    public void setEstimatedWeeklyHours(Integer estimatedWeeklyHours) { this.estimatedWeeklyHours = estimatedWeeklyHours; }

    public List<String> getSkillsToAcquire() { return skillsToAcquire; }
    public void setSkillsToAcquire(List<String> skillsToAcquire) { this.skillsToAcquire = skillsToAcquire; }

    public List<MilestoneDto> getMilestones() { return milestones; }
    public void setMilestones(List<MilestoneDto> milestones) { this.milestones = milestones; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
