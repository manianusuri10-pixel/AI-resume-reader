package com.aicopilot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rag_documents")
public class RagDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String category;

    @Lob
    @Column(columnDefinition = "CLOB", nullable = false)
    private String content;

    private String source;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String embeddingVector; // comma-separated vector coordinates

    private LocalDateTime createdAt;

    public RagDocument() {
        this.createdAt = LocalDateTime.now();
    }

    public RagDocument(String title, String category, String content, String source) {
        this.title = title;
        this.category = category;
        this.content = content;
        this.source = source;
        this.createdAt = LocalDateTime.now();
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

    public String getEmbeddingVector() { return embeddingVector; }
    public void setEmbeddingVector(String embeddingVector) { this.embeddingVector = embeddingVector; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
