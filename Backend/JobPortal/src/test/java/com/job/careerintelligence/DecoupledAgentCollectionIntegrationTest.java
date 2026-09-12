package com.job.careerintelligence;

import com.job.careerintelligence.agent.AgentExecution;
import com.job.careerintelligence.agent.AgentExecutionRepository;
import com.job.careerintelligence.agent.CareerIntelligenceAgent;
import com.job.careerintelligence.agent.CareerIntelligenceTool;
import com.job.careerintelligence.dto.CollectionMetrics;
import com.job.careerintelligence.scheduler.CareerIntelligenceScheduler;
import com.job.careerintelligence.service.DataCollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "career.intelligence.scheduler.enabled=true")
@ActiveProfiles("dev")
@Transactional
public class DecoupledAgentCollectionIntegrationTest {

    @Autowired
    private CareerIntelligenceAgent agent;

    @Autowired
    private CareerIntelligenceScheduler scheduler;

    @MockBean
    private DataCollectionService dataCollectionService;

    @MockBean
    private CareerIntelligenceTool tools;

    @Autowired
    private AgentExecutionRepository executionRepo;

    @BeforeEach
    void setUp() {
        executionRepo.deleteAll();
    }

    @Test
    public void testAgentDoesNotCallDataCollection() throws Exception {
        // Mock the observation snapshot to simulate no work
        when(tools.verifyPipelineState()).thenReturn(true);

        agent.runExecution();

        // Verify the agent does NOT invoke collectJobs
        verify(tools, never()).collectJobs(anyString());

        List<AgentExecution> executions = executionRepo.findAll();
        assertFalse(executions.isEmpty());
        assertEquals("COMPLETED", executions.get(0).getStatus());
    }

    @Test
    public void testSchedulerTriggersCollectionIndependently() {
        when(dataCollectionService.runCollectionCycle()).thenReturn(new CollectionMetrics());

        scheduler.runContinuousCollection();

        // Verify that data collection is invoked by the scheduler
        verify(dataCollectionService, times(1)).runCollectionCycle();
    }

    @Test
    public void testSchedulerCollectionFailureDoesNotCorruptAgent() {
        // Simulate collection failure
        doThrow(new RuntimeException("Simulated network failure")).when(dataCollectionService).runCollectionCycle();

        assertDoesNotThrow(() -> scheduler.runContinuousCollection());

        // Agent should still run successfully despite collection failure
        when(tools.verifyPipelineState()).thenReturn(true);
        agent.runExecution();

        List<AgentExecution> executions = executionRepo.findAll();
        assertEquals("COMPLETED", executions.get(0).getStatus());
    }
}
