package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.SemanticSimilarityResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemanticSimilarityResultRepository extends JpaRepository<SemanticSimilarityResult, Long> {
    Optional<SemanticSimilarityResult> findByCandidateProfileIdAndJobEnrichmentIdAndModelName(Long candidateProfileId, Long jobEnrichmentId, String modelName);
}
