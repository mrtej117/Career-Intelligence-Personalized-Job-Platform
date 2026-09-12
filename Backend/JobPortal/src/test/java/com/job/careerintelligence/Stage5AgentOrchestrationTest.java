package com.job.careerintelligence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.agent.*;
import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.repository.*;
import com.job.careerintelligence.llm.LocalLlmClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Commit;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("postgres")
public class Stage5AgentOrchestrationTest {

    @Autowired
    private CareerIntelligenceAgent agent;

    @Autowired
    private AgentExecutionRepository executionRepo;
    
    @MockBean
    private ObservationSnapshotService observationService;
    
    @MockBean
    private CareerIntelligenceTool tools;

    @Test
    public void testNoWork() throws Exception {
        AgentObservationSnapshot snapshot = new AgentObservationSnapshot();
        snapshot.setPostgresAvailable(true);
        snapshot.setOllamaAvailable(true);
        
        when(observationService.takeSnapshot()).thenReturn(snapshot);
        when(tools.verifyPipelineState()).thenReturn(true);
        
        agent.runExecution();
        
        AgentExecution lastExecution = getLatestExecution();
        assertEquals("COMPLETED", lastExecution.getStatus());
        assertEquals("Clean exit - no work required.", lastExecution.getTerminationReason());
        verify(tools, never()).enrichSpecificJobs(any());
    }

    @Test
    public void testNewJobRequiresEnrichment() throws Exception {
        AgentObservationSnapshot snapshot = new AgentObservationSnapshot();
        snapshot.setPostgresAvailable(true);
        snapshot.setOllamaAvailable(true);
        snapshot.getJobsToEnrich().add(1L);
        
        JobSemanticEnrichment dummyEnrichment = new JobSemanticEnrichment();
        dummyEnrichment.setId(100L);
        
        when(observationService.takeSnapshot()).thenReturn(snapshot);
        when(tools.enrichSpecificJobs(anyList())).thenReturn(List.of(dummyEnrichment));
        when(tools.verifyPipelineState()).thenReturn(true);
        
        agent.runExecution();
        
        AgentExecution lastExecution = getLatestExecution();
        assertEquals("COMPLETED", lastExecution.getStatus());
        verify(tools, times(1)).enrichSpecificJobs(anyList());
        verify(tools, times(1)).generateSpecificEmbeddings(anyList());
        verify(tools, times(1)).matchSpecificJobs(anyList());
    }

    @Test
    public void testMissingEmbedding() throws Exception {
        AgentObservationSnapshot snapshot = new AgentObservationSnapshot();
        snapshot.setPostgresAvailable(true);
        snapshot.setOllamaAvailable(true);
        snapshot.getJobsToEmbed().add(2L); // Just missing embedding
        snapshot.getJobsToMatch().add(2L); // It will need matching
        
        when(observationService.takeSnapshot()).thenReturn(snapshot);
        when(tools.verifyPipelineState()).thenReturn(true);
        
        agent.runExecution();
        
        AgentExecution lastExecution = getLatestExecution();
        assertEquals("COMPLETED", lastExecution.getStatus());
        verify(tools, never()).enrichSpecificJobs(any());
        verify(tools, times(1)).generateSpecificEmbeddings(anyList());
        verify(tools, times(1)).matchSpecificJobs(anyList());
    }

    @Test
    public void testOllamaDown() throws Exception {
        AgentObservationSnapshot snapshot = new AgentObservationSnapshot();
        snapshot.setPostgresAvailable(true);
        snapshot.setOllamaAvailable(false);
        snapshot.getJobsToEnrich().add(1L); // Requires Ollama
        
        when(observationService.takeSnapshot()).thenReturn(snapshot);
        
        agent.runExecution();
        
        AgentExecution lastExecution = getLatestExecution();
        assertEquals("FAILED", lastExecution.getStatus());
        assertTrue(lastExecution.getTerminationReason().contains("Ollama unavailable"));
        verify(tools, never()).enrichSpecificJobs(any());
    }

    @Test
    public void testCandidateChangeOnly() throws Exception {
        AgentObservationSnapshot snapshot = new AgentObservationSnapshot();
        snapshot.setPostgresAvailable(true);
        snapshot.setOllamaAvailable(true);
        snapshot.getCandidatesToMatch().add(10L);
        
        when(observationService.takeSnapshot()).thenReturn(snapshot);
        when(tools.verifyPipelineState()).thenReturn(true);
        
        agent.runExecution();
        
        AgentExecution lastExecution = getLatestExecution();
        assertEquals("COMPLETED", lastExecution.getStatus());
        verify(tools, never()).enrichSpecificJobs(any());
        verify(tools, never()).generateSpecificEmbeddings(any());
        verify(tools, times(1)).matchCandidates(anyList());
    }

    private AgentExecution getLatestExecution() {
        return executionRepo.findAll().stream()
            .max((e1, e2) -> e1.getStartedAt().compareTo(e2.getStartedAt()))
            .orElseThrow();
    }
}
