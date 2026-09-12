package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.CandidateAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateActionRepository extends JpaRepository<CandidateAction, Long> {
    List<CandidateAction> findByCandidateIdOrderByTimestampDesc(Long candidateId);
    long countByCandidateId(Long candidateId);
}
