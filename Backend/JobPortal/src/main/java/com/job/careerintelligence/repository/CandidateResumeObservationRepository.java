package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.CandidateResumeObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateResumeObservationRepository extends JpaRepository<CandidateResumeObservation, Long> {
    Optional<CandidateResumeObservation> findTopByJobSeekerIdOrderByUploadedAtDesc(Long jobSeekerId);
    Optional<CandidateResumeObservation> findByJobSeekerIdAndContentHash(Long jobSeekerId, String contentHash);
}
