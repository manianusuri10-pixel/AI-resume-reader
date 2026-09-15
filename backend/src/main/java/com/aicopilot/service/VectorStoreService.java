package com.aicopilot.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VectorStoreService {

    public static final int VECTOR_DIMENSIONS = 128;

    /**
     * Computes a normalized dense semantic embedding vector for any arbitrary text.
     * Uses character n-gram hashing and term-frequency weighting with L2 normalization.
     */
    public double[] generateEmbedding(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new double[VECTOR_DIMENSIONS];
        }

        double[] vector = new double[VECTOR_DIMENSIONS];
        String normalized = text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9\\s_+#.-]", " ");
        String[] tokens = normalized.split("\\s+");

        for (String token : tokens) {
            if (token.isEmpty()) continue;
            
            // Unigram feature
            int hash1 = Math.abs(token.hashCode()) % VECTOR_DIMENSIONS;
            vector[hash1] += 1.0;

            // Character trigrams for morphological and sub-word robustness
            if (token.length() >= 3) {
                for (int i = 0; i <= token.length() - 3; i++) {
                    String trigram = token.substring(i, i + 3);
                    int hash2 = Math.abs(trigram.hashCode() * 31) % VECTOR_DIMENSIONS;
                    vector[hash2] += 0.4;
                }
            }
        }

        // L2 normalization (Euclidean norm)
        double sumSquares = 0.0;
        for (double val : vector) {
            sumSquares += val * val;
        }

        double norm = Math.sqrt(sumSquares);
        if (norm > 0.0) {
            for (int i = 0; i < VECTOR_DIMENSIONS; i++) {
                vector[i] /= norm;
            }
        }

        return vector;
    }

    /**
     * Calculates the cosine similarity between two normalized vectors.
     * Result range: [-1.0, 1.0].
     */
    public double cosineSimilarity(double[] vectorA, double[] vectorB) {
        if (vectorA == null || vectorB == null || vectorA.length != vectorB.length) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }

        double denominator = Math.sqrt(normA) * Math.sqrt(normB);
        if (denominator == 0.0) {
            return 0.0;
        }

        return Math.max(0.0, Math.min(1.0, dotProduct / denominator));
    }

    public String vectorToString(double[] vector) {
        if (vector == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vector.length; i++) {
            sb.append(String.format(Locale.US, "%.6f", vector[i]));
            if (i < vector.length - 1) sb.append(",");
        }
        return sb.toString();
    }

    public double[] stringToVector(String str) {
        if (str == null || str.trim().isEmpty()) {
            return new double[VECTOR_DIMENSIONS];
        }
        String[] parts = str.split(",");
        double[] vector = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                vector[i] = Double.parseDouble(parts[i].trim());
            } catch (NumberFormatException e) {
                vector[i] = 0.0;
            }
        }
        return vector;
    }
}
