package com.job.careerintelligence.agent;

import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.llm.LocalLlmClient;
import com.job.careerintelligence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObservationSnapshotService {

    private final SourceJobIdentityRepository sourceRepo;
    private final RawJobObservationRepository rawRepo;
    private final JobSemanticEnrichmentRepository enrichmentRepo;
    private final SemanticEmbeddingRepository embeddingRepo;
    private final CandidateSemanticProfileRepository candidateRepo;
    private final LocalLlmClient llmClient;

    @Transactional(readOnly = true)
    public AgentObservationSnapshot takeSnapshot() {
        AgentObservationSnapshot snapshot = new AgentObservationSnapshot();
        
        snapshot.setOllamaAvailable(llmClient.isModelAvailable());
        snapshot.setPostgresAvailable(true); // If we can read this, PG is up

        List<SourceJobIdentity> activeJobs = sourceRepo.findByIsActiveTrue();
        snapshot.setActiveJobsCount(activeJobs.size());

        for (SourceJobIdentity identity : activeJobs) {
            Optional<RawJobObservation> latestRawOpt = rawRepo.findFirstByJobIdentityIdOrderByCollectionTimestampDesc(identity.getId());
            if (latestRawOpt.isEmpty()) continue;
            
            RawJobObservation latestRaw = latestRawOpt.get();
            JobSemanticEnrichment enrichment = enrichmentRepo.findByRawObservationId(latestRaw.getId());

            if (enrichment == null || !"COMPLETED".equals(enrichment.getProcessingStatus())) {
                snapshot.getJobsToEnrich().add(latestRaw.getId());
            } else {
                // Check if it has an embedding
                boolean hasEmbedding = embeddingRepo.findByEntityTypeAndEntityIdAndModelName(
                        "JOB", enrichment.getId(), llmClient.getModelName()).isPresent();
                if (!hasEmbedding) {
                    snapshot.getJobsToEmbed().add(enrichment.getId());
                }
            }
        }

        List<CandidateSemanticProfile> activeCandidates = candidateRepo.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsLatestVersion()))
                .collect(Collectors.toList());
        snapshot.setActiveCandidatesCount(activeCandidates.size());

        for (CandidateSemanticProfile candidate : activeCandidates) {
            if (!"COMPLETED".equals(candidate.getProcessingStatus())) {
                snapshot.getCandidatesToProfile().add(candidate.getId());
                continue;
            }

            boolean hasEmbedding = embeddingRepo.findByEntityTypeAndEntityIdAndModelName(
                    "CANDIDATE", candidate.getId(), llmClient.getModelName()).isPresent();
            if (!hasEmbedding) {
                snapshot.getCandidatesToEmbed().add(candidate.getId());
            }
        }

        // For targeted matching, we match newly enriched/embedded jobs to ALL candidates,
        // and newly profiled/embedded candidates to ALL jobs.
        snapshot.getJobsToMatch().addAll(snapshot.getJobsToEmbed()); // will be embedded this run
        
        // Also find candidates that have no JobMatchResult yet
        // In a full implementation, we'd check if candidate needs matching
        // For now, if candidatesToEmbed is populated, we know they need matching after
        snapshot.getCandidatesToMatch().addAll(snapshot.getCandidatesToEmbed());

        return snapshot;
    }
}
