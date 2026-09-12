package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.JobSemanticEnrichment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobSemanticEnrichmentRepository extends JpaRepository<JobSemanticEnrichment, Long> {
    JobSemanticEnrichment findByRawObservationId(Long rawObservationId);
    java.util.List<JobSemanticEnrichment> findByProcessingStatus(String processingStatus);
}
