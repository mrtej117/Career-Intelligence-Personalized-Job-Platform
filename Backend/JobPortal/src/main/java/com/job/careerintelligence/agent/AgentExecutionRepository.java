package com.job.careerintelligence.agent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentExecutionRepository extends JpaRepository<AgentExecution, Long> {
    Optional<AgentExecution> findByExecutionId(String executionId);
}
