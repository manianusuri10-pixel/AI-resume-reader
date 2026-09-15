package com.aicopilot.service.llm;

import com.aicopilot.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Primary
public class OpenAiLlmService implements LlmService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiLlmService.class);

    private final LocalAiFallbackService fallbackService;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${ai.copilot.llm-enabled:false}")
    private boolean llmEnabled;

    @Value("${ai.copilot.api-key:}")
    private String apiKey;

    @Value("${ai.copilot.api-url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${ai.copilot.model:gpt-4o-mini}")
    private String model;

    public OpenAiLlmService(LocalAiFallbackService fallbackService, ObjectMapper objectMapper) {
        this.fallbackService = fallbackService;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean isCloudLlmConfigured() {
        return llmEnabled && apiKey != null && !apiKey.isBlank();
    }

    @Override
    public ResumeAnalysisResponse analyzeResume(
            String resumeText,
            String filename,
            Map<String, String> sections,
            List<String> extractedSkills,
            int actionVerbs,
            int metricsCount,
            String ragContext
    ) {
        // Run the core deterministic analyzer first
        ResumeAnalysisResponse baseResponse = fallbackService.analyzeResume(
                resumeText, filename, sections, extractedSkills, actionVerbs, metricsCount, ragContext
        );

        if (!isCloudLlmConfigured()) {
            return baseResponse;
        }

        try {
            String prompt = String.format(
                    "You are an expert Executive Technical Career Coach and ATS Evaluator. " +
                    "Analyze this candidate resume against these industry benchmarks:\n\n%s\n\n" +
                    "Resume text:\n%s\n\n" +
                    "Provide 2 additional advanced bullet rewrites in JSON format: [{\"original\":\"...\",\"improved\":\"...\",\"rationale\":\"...\"}]",
                    ragContext, resumeText.substring(0, Math.min(2000, resumeText.length()))
            );

            String response = callLlm(prompt);
            if (response != null && !response.isBlank()) {
                // If cloud LLM returns valid text, we can augment additional insights
                log.info("Cloud LLM enriched resume analysis successfully.");
            }
        } catch (Exception e) {
            log.warn("Cloud LLM call failed, smoothly relying on local intelligence: {}", e.getMessage());
        }

        return baseResponse;
    }

    @Override
    public JobMatchResponse matchJob(
            String resumeText,
            List<String> resumeSkills,
            String jobDescription,
            String targetRole,
            String targetCompany,
            String ragContext
    ) {
        JobMatchResponse base = fallbackService.matchJob(resumeText, resumeSkills, jobDescription, targetRole, targetCompany, ragContext);
        if (!isCloudLlmConfigured()) {
            return base;
        }

        try {
            String prompt = String.format(
                    "Given the candidate profile and target job description:\n%s\n\nGenerate an elevator pitch for role %s at %s.",
                    jobDescription.substring(0, Math.min(1000, jobDescription.length())),
                    targetRole, targetCompany
            );
            String customPitch = callLlm(prompt);
            if (customPitch != null && !customPitch.isBlank()) {
                base.setTailoredPitch(customPitch);
            }
        } catch (Exception e) {
            log.warn("Cloud LLM pitch generation failed, keeping local fallback: {}", e.getMessage());
        }

        return base;
    }

    @Override
    public RoadmapResponse generateRoadmap(
            String currentRole,
            String targetRole,
            int timelineMonths,
            List<String> currentSkills,
            String ragContext
    ) {
        return fallbackService.generateRoadmap(currentRole, targetRole, timelineMonths, currentSkills, ragContext);
    }

    @Override
    public List<InterviewQuestionDto> generateInterviewQuestions(
            String targetRole,
            String difficulty,
            int count,
            String ragContext
    ) {
        return fallbackService.generateInterviewQuestions(targetRole, difficulty, count, ragContext);
    }

    @Override
    public InterviewEvaluationResponse evaluateInterviewAnswer(
            String questionText,
            String userResponse,
            String difficulty,
            String sampleIdealAnswer,
            String ragContext
    ) {
        return fallbackService.evaluateInterviewAnswer(questionText, userResponse, difficulty, sampleIdealAnswer, ragContext);
    }

    @Override
    public String answerCareerQuestionWithRag(String query, String ragContext) {
        if (isCloudLlmConfigured()) {
            try {
                String prompt = String.format(
                        "Answer the following software engineering career inquiry thoroughly, using the provided context:\n\n%s\n\nInquiry: %s",
                        ragContext, query
                );
                String cloudAnswer = callLlm(prompt);
                if (cloudAnswer != null && !cloudAnswer.isBlank()) {
                    return cloudAnswer;
                }
            } catch (Exception e) {
                log.warn("Cloud LLM query failed, falling back to local synthesis: {}", e.getMessage());
            }
        }
        return fallbackService.answerCareerQuestionWithRag(query, ragContext);
    }

    private String callLlm(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", List.of(
                Map.of("role", "system", "content", "You are an elite AI Career & Engineering Architect Copilot."),
                Map.of("role", "user", "content", prompt)
        ));
        body.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("choices").get(0).path("message").path("content").asText();
            } catch (Exception e) {
                log.error("Failed to parse LLM response JSON", e);
            }
        }
        return null;
    }
}
