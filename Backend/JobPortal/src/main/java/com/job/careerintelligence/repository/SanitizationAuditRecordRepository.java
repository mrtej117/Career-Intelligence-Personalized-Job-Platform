package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.SanitizationAuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanitizationAuditRecordRepository extends JpaRepository<SanitizationAuditRecord, Long> {
    List<SanitizationAuditRecord> findByRawObservationId(Long rawObservationId);
}
