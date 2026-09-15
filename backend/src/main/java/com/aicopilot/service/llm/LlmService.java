package com.aicopilot.service.llm;

import com.aicopilot.dto.*;

import java.util.List;
import java.util.Map;

public interface LlmService {

    boolean isCloudLlmConfigured();

    ResumeAnalysisResponse analyzeResume(
            String resumeText,
            String filename,
            Map<String, String> sections,
            List<String> extractedSkills,
            int actionVerbs,
            int metricsCount,
            String ragContext
    );

    JobMatchResponse matchJob(
            String resumeText,
            List<String> resumeSkills,
            String jobDescription,
            String targetRole,
            String targetCompany,
            String ragContext
    );

    RoadmapResponse generateRoadmap(
            String currentRole,
            String targetRole,
            int timelineMonths,
            List<String> currentSkills,
            String ragContext
    );

    List<InterviewQuestionDto> generateInterviewQuestions(
            String targetRole,
            String difficulty,
            int count,
            String ragContext
    );

    InterviewEvaluationResponse evaluateInterviewAnswer(
            String questionText,
            String userResponse,
            String difficulty,
            String sampleIdealAnswer,
            String ragContext
    );

    String answerCareerQuestionWithRag(String query, String ragContext);
}
