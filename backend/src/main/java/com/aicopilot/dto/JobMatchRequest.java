package com.aicopilot.dto;

import jakarta.validation.constraints.NotBlank;

public class JobMatchRequest {
    private Long resumeId;
    private String resumeText;
    private String targetRole;
    private String targetCompany;

    @NotBlank(message = "Job description is required")
    private String jobDescription;

    public JobMatchRequest() {}

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public String getResumeText() { return resumeText; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getTargetCompany() { return targetCompany; }
    public void setTargetCompany(String targetCompany) { this.targetCompany = targetCompany; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }
}
