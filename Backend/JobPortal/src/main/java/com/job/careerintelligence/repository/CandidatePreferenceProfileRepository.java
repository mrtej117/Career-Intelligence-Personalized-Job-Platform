package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.CandidatePreferenceProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidatePreferenceProfileRepository extends JpaRepository<CandidatePreferenceProfile, Long> {
    Optional<CandidatePreferenceProfile> findByCandidateId(Long candidateId);
}
