package com.job.careerintelligence.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class CareerIntelligenceAgent {

    private final CareerIntelligenceTool tools;
    private final AgentExecutionRepository executionRepo;
    private final AgentDecisionLogRepository decisionLogRepo;
    private final ObservationSnapshotService observationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${career.intelligence.agent.max-iterations:10}")
    private int maxIterations;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public void runExecution() {
        if (!isRunning.compareAndSet(false, true)) {
            log.warn("[AGENT] Execution already in progress. Skipping.");
            return;
        }

        AgentExecution execution = new AgentExecution();
        execution.setExecutionId(UUID.randomUUID().toString());
        execution.setStartedAt(LocalDateTime.now());
        execution.setCurrentState(AgentState.IDLE);
        execution.setStatus("RUNNING");
        execution = executionRepo.save(execution);

        try {
            orchestrate(execution);
        } catch (Exception e) {
            log.error("[AGENT] Execution failed", e);
            execution.setStatus("FAILED");
            execution.setCurrentState(AgentState.FAILED);
            execution.setError(e.getMessage());
            execution.setTerminationReason("Exception: " + e.getMessage());
        } finally {
            execution.setCompletedAt(LocalDateTime.now());
            executionRepo.save(execution);
            isRunning.set(false);
        }
    }

    private void orchestrate(AgentExecution execution) throws Exception {
        int iteration = 0;
        
        // 1. OBSERVING
        execution.setCurrentState(AgentState.OBSERVING);
        executionRepo.save(execution);
        
        // Trigger data collection removed to decouple collection from agent orchestration.
        
        // Take snapshot of current DB state AFTER collection
        AgentObservationSnapshot snapshot = observationService.takeSnapshot();
        execution.setObservationSummary(objectMapper.writeValueAsString(snapshot));
        executionRepo.save(execution);

        // 2. DECIDING
        execution.setCurrentState(AgentState.ANALYZING_CHANGES);
        executionRepo.save(execution);
        
        String decisionReason;
        AgentState nextState;

        if (!snapshot.isPostgresAvailable()) {
            decisionReason = "PostgreSQL unavailable. Terminating.";
            nextState = AgentState.FAILED;
            execution.setError("PostgreSQL unavailable");
        } else if (snapshot.hasAnyWork() && !snapshot.isOllamaAvailable()) {
            decisionReason = "Ollama unavailable but work is required. Terminating to avoid partial state.";
            nextState = AgentState.FAILED;
            execution.setError("Ollama unavailable");
        } else if (!snapshot.hasAnyWork()) {
            decisionReason = "No new, changed, or missing data observed. No work needed.";
            nextState = AgentState.VERIFYING;
        } else {
            decisionReason = String.format("Work needed: %d enrichments, %d embeddings, %d matches.", 
                snapshot.getJobsToEnrich().size(), snapshot.getJobsToEmbed().size(), snapshot.getJobsToMatch().size());
            nextState = AgentState.ENRICHING; // Start the pipeline
        }
        
        execution.setDecisionSummary(decisionReason);
        executionRepo.save(execution);
        logDecision(execution.getExecutionId(), AgentAction.WAIT, decisionReason);

        if (nextState == AgentState.FAILED) {
            execution.setStatus("FAILED");
            execution.setCurrentState(AgentState.FAILED);
            execution.setTerminationReason(decisionReason);
            return;
        }
        
        if (nextState == AgentState.VERIFYING) {
            execution.setStatus("COMPLETED");
            execution.setCurrentState(AgentState.COMPLETED);
            execution.setVerificationSummary("Verified no work required.");
            execution.setTerminationReason("Clean exit - no work required.");
            return;
        }

        // 3. ACTING
        
        // A. ENRICHING
        if (!snapshot.getJobsToEnrich().isEmpty()) {
            execution.setCurrentState(AgentState.ENRICHING);
            executionRepo.save(execution);
            
            var enriched = tools.enrichSpecificJobs(snapshot.getJobsToEnrich());
            execution.setJobsNewlyEnriched(enriched.size());
            
            // Add newly enriched jobs to the embed/match lists
            for (var e : enriched) {
                snapshot.getJobsToEmbed().add(e.getId());
                snapshot.getJobsToMatch().add(e.getId());
            }
            executionRepo.save(execution);
        }

        // B. EMBEDDING
        if (!snapshot.getJobsToEmbed().isEmpty()) {
            execution.setCurrentState(AgentState.EMBEDDING);
            executionRepo.save(execution);
            
            int embeddedCount = tools.generateSpecificEmbeddings(snapshot.getJobsToEmbed());
            execution.setJobsEmbedded(embeddedCount);
            executionRepo.save(execution);
        }

        // C. MATCHING
        if (!snapshot.getJobsToMatch().isEmpty()) {
            execution.setCurrentState(AgentState.MATCHING);
            executionRepo.save(execution);
            
            int matchedCount = tools.matchSpecificJobs(snapshot.getJobsToMatch());
            execution.setCandidatesMatched(matchedCount);
            executionRepo.save(execution);
        }
        
        if (!snapshot.getCandidatesToMatch().isEmpty()) {
            execution.setCurrentState(AgentState.MATCHING);
            executionRepo.save(execution);
            
            int matchedCount = tools.matchCandidates(snapshot.getCandidatesToMatch());
            execution.setCandidatesMatched(execution.getCandidatesMatched() + matchedCount);
            executionRepo.save(execution);
        }

        // 4. VERIFYING
        execution.setCurrentState(AgentState.VERIFYING);
        executionRepo.save(execution);
        
        boolean verified = tools.verifyPipelineState();
        execution.setVerificationSummary(verified ? "Pipeline verification passed" : "Pipeline verification failed");
        
        // 5. TERMINATING
        execution.setStatus("COMPLETED");
        execution.setCurrentState(AgentState.COMPLETED);
        execution.setTerminationReason("Successfully completed targeted orchestration.");
    }

    private void logDecision(String executionId, AgentAction decision, String reason) {
        AgentDecisionLog log = new AgentDecisionLog();
        log.setExecutionId(executionId);
        log.setDecision(decision);
        log.setReason(reason);
        log.setOutcome("EXECUTED");
        decisionLogRepo.save(log);
    }
}
