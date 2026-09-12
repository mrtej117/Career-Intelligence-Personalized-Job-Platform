package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.CandidateSemanticProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateSemanticProfileRepository extends JpaRepository<CandidateSemanticProfile, Long> {
    Optional<CandidateSemanticProfile> findTopByJobSeekerIdAndIsLatestVersionTrueOrderByProcessingTimestampDesc(Long jobSeekerId);
    Optional<CandidateSemanticProfile> findByJobSeekerIdAndContentHash(Long jobSeekerId, String contentHash);
}
