package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.HybridMatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HybridMatchResultRepository extends JpaRepository<HybridMatchResult, Long> {
    Optional<HybridMatchResult> findByCandidateProfileIdAndJobEnrichmentId(Long candidateProfileId, Long jobEnrichmentId);
    org.springframework.data.domain.Page<HybridMatchResult> findByCandidateProfileIdOrderByHybridScoreDesc(Long candidateProfileId, org.springframework.data.domain.Pageable pageable);
    java.util.List<HybridMatchResult> findByCandidateProfileIdOrderByHybridScoreDesc(Long candidateProfileId);
}
