package com.aicopilot.repository;

import com.aicopilot.model.RagDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RagDocumentRepository extends JpaRepository<RagDocument, Long> {
    List<RagDocument> findByCategory(String category);
}
