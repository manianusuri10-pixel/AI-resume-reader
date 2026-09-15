package com.aicopilot.dto;

import java.util.ArrayList;
import java.util.List;

public class RagQueryResponse {
    private String query;
    private String synthesizedAnswer;
    private List<RagChunkDto> retrievedChunks = new ArrayList<>();

    public RagQueryResponse() {}

    public RagQueryResponse(String query, String synthesizedAnswer, List<RagChunkDto> retrievedChunks) {
        this.query = query;
        this.synthesizedAnswer = synthesizedAnswer;
        this.retrievedChunks = retrievedChunks;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getSynthesizedAnswer() { return synthesizedAnswer; }
    public void setSynthesizedAnswer(String synthesizedAnswer) { this.synthesizedAnswer = synthesizedAnswer; }

    public List<RagChunkDto> getRetrievedChunks() { return retrievedChunks; }
    public void setRetrievedChunks(List<RagChunkDto> retrievedChunks) { this.retrievedChunks = retrievedChunks; }
}
