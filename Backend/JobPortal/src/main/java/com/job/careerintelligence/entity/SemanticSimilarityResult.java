package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "semantic_similarity_result", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"candidateProfileId", "jobEnrichmentId", "modelName"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemanticSimilarityResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long candidateProfileId;

    @Column(nullable = false)
    private Long jobEnrichmentId;

    @Column(nullable = false)
    private Long candidateEmbeddingId;

    @Column(nullable = false)
    private Long jobEmbeddingId;

    @Column(nullable = false)
    private Double similarityScore;

    @Column(nullable = false)
    private String modelName;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime calculatedAt;
}
