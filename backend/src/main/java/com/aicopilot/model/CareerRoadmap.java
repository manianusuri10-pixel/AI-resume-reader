package com.aicopilot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "career_roadmaps")
public class CareerRoadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "candidate_current_role")
    private String currentRole;

    private String targetRole;

    private Integer timelineMonths;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String milestonesJson;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String recommendedSkillsJson;

    private LocalDateTime createdAt;

    public CareerRoadmap() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCurrentRole() { return currentRole; }
    public void setCurrentRole(String currentRole) { this.currentRole = currentRole; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public Integer getTimelineMonths() { return timelineMonths; }
    public void setTimelineMonths(Integer timelineMonths) { this.timelineMonths = timelineMonths; }

    public String getMilestonesJson() { return milestonesJson; }
    public void setMilestonesJson(String milestonesJson) { this.milestonesJson = milestonesJson; }

    public String getRecommendedSkillsJson() { return recommendedSkillsJson; }
    public void setRecommendedSkillsJson(String recommendedSkillsJson) { this.recommendedSkillsJson = recommendedSkillsJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
