package com.aicopilot.service;

import com.aicopilot.dto.RagChunkDto;
import com.aicopilot.model.RagDocument;
import com.aicopilot.repository.RagDocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final RagDocumentRepository ragDocumentRepository;
    private final VectorStoreService vectorStoreService;

    @Value("${ai.copilot.rag.top-k:4}")
    private int defaultTopK;

    @Value("${ai.copilot.rag.similarity-threshold:0.15}")
    private double defaultMinSimilarity;

    public RagService(RagDocumentRepository ragDocumentRepository, VectorStoreService vectorStoreService) {
        this.ragDocumentRepository = ragDocumentRepository;
        this.vectorStoreService = vectorStoreService;
    }

    public RagDocument saveAndIndex(String title, String category, String content, String source) {
        double[] embedding = vectorStoreService.generateEmbedding(title + " " + category + " " + content);
        String vectorStr = vectorStoreService.vectorToString(embedding);

        RagDocument doc = new RagDocument(title, category, content, source);
        doc.setEmbeddingVector(vectorStr);
        return ragDocumentRepository.save(doc);
    }

    public List<RagChunkDto> searchSimilar(String query, Integer topK, Double minScore) {
        int limit = (topK != null && topK > 0) ? topK : defaultTopK;
        double threshold = (minScore != null) ? minScore : defaultMinSimilarity;

        double[] queryVector = vectorStoreService.generateEmbedding(query);
        List<RagDocument> allDocs = ragDocumentRepository.findAll();

        List<RagChunkDto> scoredChunks = new ArrayList<>();
        for (RagDocument doc : allDocs) {
            double[] docVector;
            if (doc.getEmbeddingVector() != null && !doc.getEmbeddingVector().isEmpty()) {
                docVector = vectorStoreService.stringToVector(doc.getEmbeddingVector());
            } else {
                docVector = vectorStoreService.generateEmbedding(doc.getTitle() + " " + doc.getContent());
            }

            double similarity = vectorStoreService.cosineSimilarity(queryVector, docVector);
            if (similarity >= threshold) {
                scoredChunks.add(new RagChunkDto(
                        doc.getId(),
                        doc.getTitle(),
                        doc.getCategory(),
                        doc.getContent(),
                        doc.getSource(),
                        Math.round(similarity * 100.0) / 100.0
                ));
            }
        }

        // Sort descending by similarity score
        scoredChunks.sort((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()));

        return scoredChunks.stream().limit(limit).collect(Collectors.toList());
    }

    public String buildRagContext(String query, int topK) {
        List<RagChunkDto> chunks = searchSimilar(query, topK, 0.10);
        if (chunks.isEmpty()) {
            return "";
        }

        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("--- RETRIEVED KNOWLEDGE BASE CONTEXT (RAG) ---\n");
        for (int i = 0; i < chunks.size(); i++) {
            RagChunkDto chunk = chunks.get(i);
            contextBuilder.append(String.format("[%d] %s (%s, Score: %.2f):\n%s\n\n",
                    i + 1, chunk.getTitle(), chunk.getCategory(), chunk.getSimilarityScore(), chunk.getContent()));
        }
        contextBuilder.append("--- END CONTEXT ---\n");
        return contextBuilder.toString();
    }
}
