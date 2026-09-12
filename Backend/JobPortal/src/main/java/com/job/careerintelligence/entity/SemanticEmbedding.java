package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "semantic_embedding")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemanticEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityType; // "JOB" or "CANDIDATE"

    @Column(nullable = false)
    private Long entityId; // The ID of the JobSemanticEnrichment or CandidateSemanticProfile

    @Column(nullable = false)
    private String modelName; // e.g. "nomic-embed-text"

    @Column(nullable = false)
    private String sourceHash; // SHA-256 of the text used to generate this embedding to support reuse

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private double[] vector;

    @Column(nullable = false)
    private int dimensions;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
