package com.aicopilot.service;

import com.aicopilot.dto.ResumeAnalysisResponse;
import com.aicopilot.model.Resume;
import com.aicopilot.model.User;
import com.aicopilot.repository.ResumeRepository;
import com.aicopilot.repository.UserRepository;
import com.aicopilot.service.llm.LlmService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ResumeAnalysisService {

    private final DocumentParserService parserService;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final RagService ragService;
    private final LlmService llmService;
    private final ObjectMapper objectMapper;

    public ResumeAnalysisService(DocumentParserService parserService,
                                 ResumeRepository resumeRepository,
                                 UserRepository userRepository,
                                 RagService ragService,
                                 LlmService llmService,
                                 ObjectMapper objectMapper) {
        this.parserService = parserService;
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.ragService = ragService;
        this.llmService = llmService;
        this.objectMapper = objectMapper;
    }

    public ResumeAnalysisResponse analyzeAndSave(MultipartFile file, String userEmail) throws IOException {
        String text = parserService.extractText(file);
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "uploaded-resume.pdf";
        String fileType = filename.endsWith(".pdf") ? "application/pdf" : "text/plain";
        return processAndPersistResume(text, filename, fileType, userEmail);
    }

    public ResumeAnalysisResponse analyzeRawText(String rawText, String filename, String userEmail) {
        return processAndPersistResume(rawText, filename != null ? filename : "resume.txt", "text/plain", userEmail);
    }

    private ResumeAnalysisResponse processAndPersistResume(String text, String filename, String fileType, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        Map<String, String> sections = parserService.extractSections(text);
        List<String> skills = parserService.extractSkills(text);
        int actionVerbs = parserService.countActionVerbs(text);
        int metricsCount = parserService.countQuantifiableMetrics(text);

        // Fetch RAG context on ATS standards
        String ragContext = ragService.buildRagContext("ATS scoring standards and high impact resume phrasing rubrics", 3);

        // Analyze via LLM / local engine
        ResumeAnalysisResponse response = llmService.analyzeResume(
                text, filename, sections, skills, actionVerbs, metricsCount, ragContext
        );

        // Persist in DB
        Resume resume = new Resume();
        resume.setUser(user);
        resume.setFileName(filename);
        resume.setFileType(fileType);
        resume.setRawText(text);
        resume.setAtsScore(response.getAtsScore());

        try {
            resume.setParsedSkills(objectMapper.writeValueAsString(skills));
            resume.setFeedbackJson(objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            resume.setParsedSkills("[]");
            resume.setFeedbackJson("{}");
        }

        Resume saved = resumeRepository.save(resume);
        response.setResumeId(saved.getId());

        return response;
    }

    public List<Resume> getUserResumes(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));
        return resumeRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public ResumeAnalysisResponse getResumeAnalysis(Long resumeId, String userEmail) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with id: " + resumeId));

        if (!resume.getUser().getEmail().equals(userEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized access to resume.");
        }

        try {
            return objectMapper.readValue(resume.getFeedbackJson(), ResumeAnalysisResponse.class);
        } catch (Exception e) {
            // Fallback reconstruct
            Map<String, String> sections = parserService.extractSections(resume.getRawText());
            List<String> skills = parserService.extractSkills(resume.getRawText());
            ResumeAnalysisResponse res = llmService.analyzeResume(
                    resume.getRawText(),
                    resume.getFileName(),
                    sections,
                    skills,
                    parserService.countActionVerbs(resume.getRawText()),
                    parserService.countQuantifiableMetrics(resume.getRawText()),
                    ""
            );
            res.setResumeId(resume.getId());
            return res;
        }
    }
}
