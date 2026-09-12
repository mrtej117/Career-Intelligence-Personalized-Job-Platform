package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.SemanticEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemanticEmbeddingRepository extends JpaRepository<SemanticEmbedding, Long> {
    Optional<SemanticEmbedding> findByEntityTypeAndSourceHashAndModelName(String entityType, String sourceHash, String modelName);
    Optional<SemanticEmbedding> findByEntityTypeAndEntityIdAndModelName(String entityType, Long entityId, String modelName);
}
