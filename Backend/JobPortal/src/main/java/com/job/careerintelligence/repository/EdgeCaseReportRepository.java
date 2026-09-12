package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.EdgeCaseReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EdgeCaseReportRepository extends JpaRepository<EdgeCaseReport, Long> {}
