package com.aicopilot.controller;

import com.aicopilot.dto.RagChunkDto;
import com.aicopilot.dto.RagQueryRequest;
import com.aicopilot.dto.RagQueryResponse;
import com.aicopilot.model.RagDocument;
import com.aicopilot.service.RagService;
import com.aicopilot.service.llm.LlmService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RagSearchController {

    private final RagService ragService;
    private final LlmService llmService;

    public RagSearchController(RagService ragService, LlmService llmService) {
        this.ragService = ragService;
        this.llmService = llmService;
    }

    @PostMapping("/search")
    public ResponseEntity<List<RagChunkDto>> searchKnowledgeBase(@Valid @RequestBody RagQueryRequest request) {
        List<RagChunkDto> results = ragService.searchSimilar(request.getQuery(), request.getTopK(), 0.10);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/ask")
    public ResponseEntity<RagQueryResponse> askWithRag(@Valid @RequestBody RagQueryRequest request) {
        List<RagChunkDto> chunks = ragService.searchSimilar(request.getQuery(), request.getTopK(), 0.10);
        String ragContext = ragService.buildRagContext(request.getQuery(), request.getTopK());
        String answer = llmService.answerCareerQuestionWithRag(request.getQuery(), ragContext);

        RagQueryResponse response = new RagQueryResponse(request.getQuery(), answer, chunks);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ingest")
    public ResponseEntity<RagDocument> ingestDocument(@RequestBody Map<String, String> payload) {
        String title = payload.getOrDefault("title", "Custom Document");
        String category = payload.getOrDefault("category", "CUSTOM");
        String content = payload.get("content");
        String source = payload.getOrDefault("source", "User Ingestion");

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Document content cannot be empty.");
        }

        RagDocument doc = ragService.saveAndIndex(title, category, content, source);
        return ResponseEntity.ok(doc);
    }
}
