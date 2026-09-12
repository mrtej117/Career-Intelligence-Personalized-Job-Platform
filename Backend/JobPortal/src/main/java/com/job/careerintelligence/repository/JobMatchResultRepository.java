package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.JobMatchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobMatchResultRepository extends JpaRepository<JobMatchResult, Long> {
    Optional<JobMatchResult> findByCandidateProfileIdAndJobEnrichmentId(Long candidateProfileId, Long jobEnrichmentId);
    Page<JobMatchResult> findByCandidateProfileIdOrderByOverallScoreDesc(Long candidateProfileId, Pageable pageable);
    List<JobMatchResult> findByCandidateProfileIdOrderByOverallScoreDesc(Long candidateProfileId);
    List<JobMatchResult> findByJobEnrichmentIdOrderByOverallScoreDesc(Long jobEnrichmentId);
}
