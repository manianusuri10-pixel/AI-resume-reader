package com.aicopilot.service;

import com.aicopilot.dto.*;
import com.aicopilot.model.InterviewQuestion;
import com.aicopilot.model.InterviewSession;
import com.aicopilot.model.User;
import com.aicopilot.repository.InterviewQuestionRepository;
import com.aicopilot.repository.InterviewSessionRepository;
import com.aicopilot.repository.UserRepository;
import com.aicopilot.service.llm.LlmService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewService {

    private final InterviewSessionRepository sessionRepository;
    private final InterviewQuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final RagService ragService;
    private final LlmService llmService;

    public InterviewService(InterviewSessionRepository sessionRepository,
                            InterviewQuestionRepository questionRepository,
                            UserRepository userRepository,
                            RagService ragService,
                            LlmService llmService) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.ragService = ragService;
        this.llmService = llmService;
    }

    @Transactional
    public InterviewSessionResponse startSession(InterviewStartRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        String targetRole = (request.getTargetRole() != null && !request.getTargetRole().isBlank())
                ? request.getTargetRole()
                : (user.getTargetRole() != null ? user.getTargetRole() : "Senior Java & Spring Boot Engineer");

        String difficulty = request.getDifficulty() != null ? request.getDifficulty().toUpperCase() : "MID";
        int count = request.getQuestionCount() != null ? request.getQuestionCount() : 3;

        String ragContext = ragService.buildRagContext("Interview rubric and questions for " + targetRole + " " + difficulty, 3);

        List<InterviewQuestionDto> generated = llmService.generateInterviewQuestions(targetRole, difficulty, count, ragContext);

        InterviewSession session = new InterviewSession();
        session.setUser(user);
        session.setTargetRole(targetRole);
        session.setDifficulty(difficulty);
        session.setStatus("IN_PROGRESS");
        session.setTotalScore(0.0);

        for (InterviewQuestionDto dto : generated) {
            InterviewQuestion q = new InterviewQuestion();
            q.setSession(session);
            q.setQuestionNumber(dto.getQuestionNumber());
            q.setQuestionText(dto.getQuestionText());
            q.setCategory(dto.getCategory());
            q.setSampleIdealAnswer(dto.getSampleIdealAnswer());
            session.getQuestions().add(q);
        }

        InterviewSession saved = sessionRepository.save(session);
        return new InterviewSessionResponse(saved);
    }

    @Transactional
    public InterviewSessionResponse submitAnswer(InterviewSubmitAnswerRequest request, String userEmail) {
        InterviewSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Interview session not found: " + request.getSessionId()));

        if (!session.getUser().getEmail().equals(userEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized session access.");
        }

        InterviewQuestion question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + request.getQuestionId()));

        if (!question.getSession().getId().equals(session.getId())) {
            throw new IllegalArgumentException("Question does not belong to the active session.");
        }

        String ragContext = ragService.buildRagContext("Evaluation rubric for " + question.getCategory() + " " + session.getDifficulty(), 2);

        InterviewEvaluationResponse eval = llmService.evaluateInterviewAnswer(
                question.getQuestionText(),
                request.getAnswer(),
                session.getDifficulty(),
                question.getSampleIdealAnswer(),
                ragContext
        );

        question.setUserResponse(request.getAnswer());
        question.setAiScore(eval.getScore());
        question.setAiCritique(eval.getCritique());
        question.setSuggestedImprovement(eval.getSuggestedImprovement());
        question.setEvaluatedAt(LocalDateTime.now());
        questionRepository.save(question);

        // Check if all questions in session are answered
        List<InterviewQuestion> allQuestions = questionRepository.findBySessionIdOrderByQuestionNumberAsc(session.getId());
        boolean allAnswered = allQuestions.stream().allMatch(q -> q.getUserResponse() != null && !q.getUserResponse().isBlank());

        double avgScore = allQuestions.stream()
                .filter(q -> q.getAiScore() != null)
                .mapToDouble(InterviewQuestion::getAiScore)
                .average()
                .orElse(0.0);

        session.setTotalScore(Math.round(avgScore * 10.0) / 10.0);

        if (allAnswered) {
            session.setStatus("COMPLETED");
            if (session.getTotalScore() >= 8.0) {
                session.setFeedbackSummary("Strong Performance: Demonstrated staff-level technical depth and articulate system reasoning.");
            } else if (session.getTotalScore() >= 6.0) {
                session.setFeedbackSummary("Competent Performance: Solid architectural foundation with room for improvement in low-level trade-offs.");
            } else {
                session.setFeedbackSummary("Needs Review: Brush up on core architectural concepts, internal mechanics, and practical examples.");
            }
        }

        session.setQuestions(allQuestions);
        InterviewSession updated = sessionRepository.save(session);

        return new InterviewSessionResponse(updated);
    }

    public InterviewSessionResponse getSession(Long sessionId, String userEmail) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Interview session not found: " + sessionId));

        if (!session.getUser().getEmail().equals(userEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized session access.");
        }

        return new InterviewSessionResponse(session);
    }

    public List<InterviewSessionResponse> getUserSessions(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        return sessionRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(InterviewSessionResponse::new)
                .collect(Collectors.toList());
    }
}
