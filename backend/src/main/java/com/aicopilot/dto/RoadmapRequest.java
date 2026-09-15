package com.aicopilot.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class RoadmapRequest {
    private String currentRole;

    @NotBlank(message = "Target role is required")
    private String targetRole;

    private Integer timelineMonths = 6;

    private List<String> currentSkills = new ArrayList<>();

    public RoadmapRequest() {}

    public String getCurrentRole() { return currentRole; }
    public void setCurrentRole(String currentRole) { this.currentRole = currentRole; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public Integer getTimelineMonths() { return timelineMonths; }
    public void setTimelineMonths(Integer timelineMonths) { this.timelineMonths = timelineMonths; }

    public List<String> getCurrentSkills() { return currentSkills; }
    public void setCurrentSkills(List<String> currentSkills) { this.currentSkills = currentSkills; }
}
