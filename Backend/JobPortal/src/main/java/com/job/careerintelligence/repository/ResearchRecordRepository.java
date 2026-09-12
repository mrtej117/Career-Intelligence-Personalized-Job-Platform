package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.ResearchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResearchRecordRepository extends JpaRepository<ResearchRecord, Long> {
}
