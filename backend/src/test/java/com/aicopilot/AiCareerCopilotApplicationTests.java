package com.aicopilot;

import com.aicopilot.config.JwtService;
import com.aicopilot.dto.ResumeAnalysisResponse;
import com.aicopilot.service.DocumentParserService;
import com.aicopilot.service.VectorStoreService;
import com.aicopilot.service.llm.LocalAiFallbackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCareerCopilotApplicationTests {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private VectorStoreService vectorStoreService;

    @Autowired
    private DocumentParserService documentParserService;

    @Autowired
    private LocalAiFallbackService localAiFallbackService;

    @Test
    void contextLoads() {
        assertNotNull(jwtService);
        assertNotNull(vectorStoreService);
        assertNotNull(documentParserService);
    }

    @Test
    void testJwtTokenLifecycle() {
        String email = "engineer@example.com";
        String token = jwtService.generateToken(email);
        assertNotNull(token);
        assertEquals(email, jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, email));
    }

    @Test
    void testVectorStoreCosineSimilarity() {
        double[] v1 = vectorStoreService.generateEmbedding("Spring Boot microservices and Java concurrency");
        double[] v2 = vectorStoreService.generateEmbedding("Java backend development with Spring microservices");
        double[] v3 = vectorStoreService.generateEmbedding("Culinary baking recipes and pastry arts");

        double sim12 = vectorStoreService.cosineSimilarity(v1, v2);
        double sim13 = vectorStoreService.cosineSimilarity(v1, v3);

        assertTrue(sim12 > sim13, "Related technical terms must have higher cosine similarity than unrelated ones");
        assertTrue(sim12 > 0.4, "Related technical terms should show solid cosine similarity");
    }

    @Test
    void testDocumentParserMetrics() {
        String sampleText = "Architected high-throughput service reducing p99 latency by 35% across 10,000 users and saving $45,000 annually.";
        int actionVerbs = documentParserService.countActionVerbs(sampleText);
        int metrics = documentParserService.countQuantifiableMetrics(sampleText);
        List<String> skills = documentParserService.extractSkills("Expert in Java, Spring Boot, React, Docker, and AWS");

        assertTrue(actionVerbs >= 1, "Should identify action verb 'Architected'");
        assertTrue(metrics >= 2, "Should identify quantifiable percentage, users, and currency");
        assertTrue(skills.contains("Java"));
        assertTrue(skills.contains("Spring Boot"));
        assertTrue(skills.contains("React"));
    }

    @Test
    void testResumeAnalysisScoring() {
        String resumeText = "SUMMARY: Senior engineer with 5 years experience.\nSKILLS: Java, Spring Boot, Docker, SQL, AWS.\nEXPERIENCE: Spearheaded backend migration, boosting throughput by 40% for 50,000 users.\nEDUCATION: BS Computer Science.";
        Map<String, String> sections = documentParserService.extractSections(resumeText);
        List<String> skills = documentParserService.extractSkills(resumeText);

        ResumeAnalysisResponse res = localAiFallbackService.analyzeResume(
                resumeText, "test-resume.txt", sections, skills, 2, 2, ""
        );

        assertNotNull(res);
        assertTrue(res.getAtsScore() >= 50);
        assertFalse(res.getStrengths().isEmpty());
        assertFalse(res.getCriticalImprovements().isEmpty());
    }
}
