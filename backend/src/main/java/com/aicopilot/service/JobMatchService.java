package com.aicopilot.service;

import com.aicopilot.dto.JobMatchRequest;
import com.aicopilot.dto.JobMatchResponse;
import com.aicopilot.model.JobMatch;
import com.aicopilot.model.Resume;
import com.aicopilot.model.User;
import com.aicopilot.repository.JobMatchRepository;
import com.aicopilot.repository.ResumeRepository;
import com.aicopilot.repository.UserRepository;
import com.aicopilot.service.llm.LlmService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobMatchService {

    private final JobMatchRepository jobMatchRepository;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final DocumentParserService parserService;
    private final RagService ragService;
    private final LlmService llmService;
    private final ObjectMapper objectMapper;

    public JobMatchService(JobMatchRepository jobMatchRepository,
                           ResumeRepository resumeRepository,
                           UserRepository userRepository,
                           DocumentParserService parserService,
                           RagService ragService,
                           LlmService llmService,
                           ObjectMapper objectMapper) {
        this.jobMatchRepository = jobMatchRepository;
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.parserService = parserService;
        this.ragService = ragService;
        this.llmService = llmService;
        this.objectMapper = objectMapper;
    }

    public JobMatchResponse matchJobDescription(JobMatchRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        String resumeText = "";
        Resume resume = null;

        if (request.getResumeId() != null) {
            resume = resumeRepository.findById(request.getResumeId())
                    .orElseThrow(() -> new IllegalArgumentException("Resume not found: " + request.getResumeId()));
            resumeText = resume.getRawText();
        } else if (request.getResumeText() != null && !request.getResumeText().isBlank()) {
            resumeText = request.getResumeText();
        } else {
            // Check if user has an uploaded resume
            List<Resume> userResumes = resumeRepository.findByUserOrderByCreatedAtDesc(user);
            if (!userResumes.isEmpty()) {
                resume = userResumes.get(0);
                resumeText = resume.getRawText();
            } else {
                throw new IllegalArgumentException("Please provide a resume or upload one first.");
            }
        }

        List<String> resumeSkills = parserService.extractSkills(resumeText);
        String targetRole = (request.getTargetRole() != null && !request.getTargetRole().isBlank())
                ? request.getTargetRole()
                : (user.getTargetRole() != null ? user.getTargetRole() : "Software Engineer");

        String ragContext = ragService.buildRagContext("Technical Competencies for " + targetRole, 2);

        JobMatchResponse response = llmService.matchJob(
                resumeText,
                resumeSkills,
                request.getJobDescription(),
                targetRole,
                request.getTargetCompany(),
                ragContext
        );

        // Persist match in database
        JobMatch jobMatch = new JobMatch();
        jobMatch.setUser(user);
        jobMatch.setResume(resume);
        jobMatch.setTargetRole(response.getTargetRole());
        jobMatch.setTargetCompany(response.getTargetCompany());
        jobMatch.setJobDescription(request.getJobDescription());
        jobMatch.setMatchScore(response.getMatchScore());
        try {
            jobMatch.setMatchedSkills(objectMapper.writeValueAsString(response.getMatchedSkills()));
            jobMatch.setMissingSkills(objectMapper.writeValueAsString(response.getMissingSkills()));
            jobMatch.setRecommendations(objectMapper.writeValueAsString(response.getRecommendations()));
        } catch (JsonProcessingException e) {
            jobMatch.setMatchedSkills("[]");
            jobMatch.setMissingSkills("[]");
            jobMatch.setRecommendations("[]");
        }
        jobMatch.setTailoredPitch(response.getTailoredPitch());

        JobMatch saved = jobMatchRepository.save(jobMatch);
        response.setMatchId(saved.getId());

        return response;
    }

    public List<JobMatch> getUserJobMatches(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));
        return jobMatchRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
