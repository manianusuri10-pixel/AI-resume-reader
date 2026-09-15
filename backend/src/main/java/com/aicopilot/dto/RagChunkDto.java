package com.aicopilot.dto;

public class RagChunkDto {
    private Long id;
    private String title;
    private String category;
    private String content;
    private String source;
    private Double similarityScore;

    public RagChunkDto() {}

    public RagChunkDto(Long id, String title, String category, String content, String source, Double similarityScore) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.content = content;
        this.source = source;
        this.similarityScore = similarityScore;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(Double similarityScore) { this.similarityScore = similarityScore; }
}
