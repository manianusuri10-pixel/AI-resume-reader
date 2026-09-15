package com.aicopilot.dto;

import jakarta.validation.constraints.NotBlank;

public class RagQueryRequest {
    @NotBlank(message = "Query is required")
    private String query;

    private Integer topK = 4;

    public RagQueryRequest() {}

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public Integer getTopK() { return topK; }
    public void setTopK(Integer topK) { this.topK = topK; }
}
