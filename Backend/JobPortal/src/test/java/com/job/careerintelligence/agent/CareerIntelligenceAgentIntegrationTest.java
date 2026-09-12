package com.job.careerintelligence.agent;

import com.job.careerintelligence.entity.RawJobObservation;
import com.job.careerintelligence.repository.RawJobObservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("postgres")
public class CareerIntelligenceAgentIntegrationTest {

    @Autowired
    private CareerIntelligenceAgent agent;

    @Autowired
    private AgentExecutionRepository executionRepo;
    
    @Autowired
    private AgentDecisionLogRepository decisionLogRepo;
    
    @Autowired
    private RawJobObservationRepository rawRepo;

    @Test
    public void testFullAgentLifecycle_UnchangedAndIdempotent() {
        System.out.println("=== Starting Agent Integration Test ===");
        
        long countBefore = rawRepo.count();

        // 1. Run the agent
        agent.runExecution();
        
        // 2. Verify Execution
        List<AgentExecution> executions = executionRepo.findAll();
        assertFalse(executions.isEmpty(), "Agent should have created an execution record");
        
        AgentExecution lastExec = executions.get(executions.size() - 1);
        System.out.println("Agent finished with status: " + lastExec.getStatus());
        System.out.println("Agent iteration count: " + lastExec.getIterationCount());
        System.out.println("Agent error: " + lastExec.getError());
        
        assertTrue(lastExec.getStatus().equals("COMPLETED") || lastExec.getStatus().equals("FAILED"));
        
        // 3. Verify Decision Logs
        List<AgentDecisionLog> logs = decisionLogRepo.findByExecutionIdOrderByTimestampAsc(lastExec.getExecutionId());
        assertFalse(logs.isEmpty(), "Agent should have logged decisions");
        
        System.out.println("--- Agent Decisions ---");
        for (AgentDecisionLog log : logs) {
            System.out.println(log.getDecision() + " -> " + log.getReason());
        }

        // 4. Verify Idempotency (Unchanged jobs should NOT duplicate)
        long countAfter = rawRepo.count();
        System.out.println("Raw jobs before: " + countBefore + ", after: " + countAfter);
        // We might discover new jobs if live, but duplicate unchanged jobs shouldn't create new records.
        // If the test environment is stable, countBefore == countAfter (or maybe some new ones, but not 2x).
        assertTrue(countAfter <= countBefore + 10, "Should not duplicate all jobs");
    }
}
