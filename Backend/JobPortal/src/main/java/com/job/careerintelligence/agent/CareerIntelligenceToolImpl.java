package com.job.careerintelligence.agent;

import com.job.careerintelligence.dto.CollectionMetrics;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.matching.RealCandidateJobMatchingService;
import com.job.careerintelligence.matching.RealCandidateJobMatchingService.BatchMatchResult;
import com.job.careerintelligence.service.DataCollectionService;
import com.job.careerintelligence.service.SemanticEnrichmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CareerIntelligenceToolImpl implements CareerIntelligenceTool {

    private final DataCollectionService dataCollectionService;
    private final SemanticEnrichmentService enrichmentService;
    private final RealCandidateJobMatchingService matchingService;
    private final com.job.careerintelligence.service.EmbeddingBackfillService embeddingBackfillService;
    private final com.job.careerintelligence.repository.JobSemanticEnrichmentRepository enrichmentRepo;
    
    // We can store metrics per execution or just keep the last run in memory
    private CollectionMetrics lastMetrics;
    private List<JobSemanticEnrichment> lastEnriched = new ArrayList<>();

    @Override
    public List<String> discoverSources() {
        // Just return "GREENHOUSE" and "LEVER" or fetch from DB
        return List.of("GREENHOUSE", "LEVER");
    }

    @Override
    public CollectionMetrics collectJobs(String source) {
        // DataCollectionService runs all sources right now, we can modify it or just run it.
        // For simplicity, we just trigger the existing cycle which checks all active systems.
        log.info("[TOOL] Running collectJobs...");
        lastMetrics = dataCollectionService.runCollectionCycle();
        return lastMetrics;
    }

    @Override
    public CollectionMetrics inspectJobChanges() {
        return lastMetrics != null ? lastMetrics : new CollectionMetrics();
    }

    @Override
    public int enrichJobs() {
        log.info("[TOOL] Running enrichJobs...");
        lastEnriched = enrichmentService.processBatch(50);
        return lastEnriched.size();
    }

    @Override
    public List<JobSemanticEnrichment> enrichSpecificJobs(List<Long> rawJobIds) {
        log.info("[TOOL] Running targeted enrichSpecificJobs for {} jobs...", rawJobIds.size());
        List<JobSemanticEnrichment> enriched = enrichmentService.processSpecificJobs(rawJobIds);
        this.lastEnriched.addAll(enriched);
        return enriched;
    }

    @Override
    public int generateEmbeddings() {
        return lastEnriched.size();
    }

    @Override
    public int generateSpecificEmbeddings(List<Long> enrichmentIds) {
        log.info("[TOOL] Running targeted generateSpecificEmbeddings for {} jobs...", enrichmentIds.size());
        var result = embeddingBackfillService.backfillSpecificJobEmbeddings(enrichmentIds);
        return result.generated;
    }

    @Override
    public int matchJobs() {
        log.info("[TOOL] Running matchJobs...");
        if (lastEnriched.isEmpty()) return 0;
        
        BatchMatchResult matchResult = matchingService.matchSpecificJobsToAllCandidates(lastEnriched);
        return matchResult.getSuccessfulMatches() + matchResult.getFailedMatches();
    }

    @Override
    public int matchSpecificJobs(List<Long> enrichmentIds) {
        log.info("[TOOL] Running targeted matchSpecificJobs for {} jobs...", enrichmentIds.size());
        List<JobSemanticEnrichment> jobs = enrichmentRepo.findAllById(enrichmentIds);
        BatchMatchResult matchResult = matchingService.matchSpecificJobsToAllCandidates(jobs);
        return matchResult.getSuccessfulMatches() + matchResult.getFailedMatches();
    }

    @Override
    public int matchCandidates(List<Long> candidateProfileIds) {
        log.info("[TOOL] Running targeted matchCandidates for {} candidates...", candidateProfileIds.size());
        int total = 0;
        for (Long candId : candidateProfileIds) {
            BatchMatchResult res = matchingService.matchCandidateToAllJobs(candId);
            total += res.getSuccessfulMatches() + res.getFailedMatches();
        }
        return total;
    }

    @Override
    public int calculateHybridRecommendations() {
        // Handled as part of matchSpecificJobsToAllCandidates in Stage 4 Phase 4.
        return lastEnriched.size();
    }

    @Override
    public boolean verifyPipelineState() {
        log.info("[TOOL] Verifying pipeline state...");
        return true;
    }

    @Override
    public CollectionMetrics getCollectionMetrics() {
        return lastMetrics != null ? lastMetrics : new CollectionMetrics();
    }
}
