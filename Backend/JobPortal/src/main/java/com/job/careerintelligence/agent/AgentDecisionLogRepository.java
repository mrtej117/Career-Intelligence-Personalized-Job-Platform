package com.job.careerintelligence.agent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentDecisionLogRepository extends JpaRepository<AgentDecisionLog, Long> {
    List<AgentDecisionLog> findByExecutionIdOrderByTimestampAsc(String executionId);
}
