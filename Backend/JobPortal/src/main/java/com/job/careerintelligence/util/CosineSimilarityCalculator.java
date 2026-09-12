package com.job.careerintelligence.util;

public class CosineSimilarityCalculator {

    /**
     * Calculates the deterministic cosine similarity between two vectors.
     * Returns a normalized score between 0 and 100.
     * 
     * Identical vectors -> 100
     * Orthogonal vectors -> 0
     * Opposite vectors -> 0 (negative values are clipped to 0 since we only care about positive relevance)
     */
    public static double calculate(double[] vectorA, double[] vectorB) {
        if (vectorA == null || vectorB == null) return 0.0;
        if (vectorA.length == 0 || vectorB.length == 0) return 0.0;
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must have the same dimensions");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        double cosineSimilarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        
        // Normalize to 0 - 100. Clip negatives to 0.
        if (cosineSimilarity < 0) {
            cosineSimilarity = 0.0;
        } else if (cosineSimilarity > 1.0) {
            cosineSimilarity = 1.0;
        }

        return cosineSimilarity * 100.0;
    }
}
