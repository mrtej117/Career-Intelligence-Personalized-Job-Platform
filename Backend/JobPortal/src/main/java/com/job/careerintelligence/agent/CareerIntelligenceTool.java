package com.job.careerintelligence.agent;

import com.job.careerintelligence.dto.CollectionMetrics;

import java.util.List;

public interface CareerIntelligenceTool {
    
    // Controlled operations for the agent
    
    List<String> discoverSources();
    
    CollectionMetrics collectJobs(String source);
    
    CollectionMetrics inspectJobChanges(); // Or just get the metrics from collect
    
    int enrichJobs();
    List<com.job.careerintelligence.entity.JobSemanticEnrichment> enrichSpecificJobs(List<Long> rawJobIds);
    
    // Generates embeddings (could be part of enrichJobs, but exposed for completeness)
    int generateEmbeddings();
    int generateSpecificEmbeddings(List<Long> enrichmentIds);
    
    int matchJobs();
    int matchSpecificJobs(List<Long> enrichmentIds);
    int matchCandidates(List<Long> candidateProfileIds);
    
    int calculateHybridRecommendations();
    
    boolean verifyPipelineState();
    
    CollectionMetrics getCollectionMetrics();
}
