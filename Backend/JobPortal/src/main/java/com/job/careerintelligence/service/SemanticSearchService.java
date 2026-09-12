package com.job.careerintelligence.service;

import com.job.careerintelligence.entity.CandidateSemanticProfile;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.entity.SemanticEmbedding;
import com.job.careerintelligence.repository.CandidateSemanticProfileRepository;
import com.job.careerintelligence.repository.JobSemanticEnrichmentRepository;
import com.job.careerintelligence.repository.SemanticEmbeddingRepository;
import com.job.careerintelligence.util.CosineSimilarityCalculator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SemanticSearchService {

    private final EmbeddingService embeddingService;
    private final SemanticEmbeddingRepository embeddingRepo;
    private final CandidateSemanticProfileRepository candidateRepo;
    private final JobSemanticEnrichmentRepository jobRepo;

    /**
     * Searches for jobs semantically similar to a candidate.
     * Uses pre-calculated embeddings. 
     */
    @Transactional(readOnly = true)
    public List<SemanticSearchResult> searchSimilarJobs(Long candidateProfileId, int topK) {
        CandidateSemanticProfile candidate = candidateRepo.findById(candidateProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate profile not found"));
                
        // Fetch candidate embedding
        Optional<SemanticEmbedding> candEmbOpt = embeddingRepo.findByEntityTypeAndEntityIdAndModelName(
                "CANDIDATE", candidateProfileId, "nomic-embed-text"); // Use configurable model if needed
                
        if (candEmbOpt.isEmpty()) {
            log.warn("No embedding found for candidate ID {}", candidateProfileId);
            return new ArrayList<>();
        }
        
        double[] candVector = candEmbOpt.get().getVector();
        
        return rankJobsAgainstVector(candVector, topK);
    }
    
    /**
     * Searches for jobs semantically similar to an arbitrary text query.
     */
    @Transactional
    public List<SemanticSearchResult> searchSimilarJobsByText(String query, int topK) {
        // We use a dummy entity type for ad-hoc queries, or just call embed directly without saving
        // Wait, the embeddingService saves it. Let's just use the provider to embed without saving for ad-hoc queries.
        // Actually, we can use embeddingService.generateAndSaveEmbedding("QUERY", hash, query) if we want to cache it.
        // Let's just generate without saving to avoid DB bloat for random searches, or just save it.
        // I will use embeddingService to save it with entityId = 0.
        SemanticEmbedding queryEmb = embeddingService.generateAndSaveEmbedding("QUERY", (long) query.hashCode(), query);
        if (queryEmb == null) return new ArrayList<>();
        
        return rankJobsAgainstVector(queryEmb.getVector(), topK);
    }

    private List<SemanticSearchResult> rankJobsAgainstVector(double[] queryVector, int topK) {
        List<JobSemanticEnrichment> allJobs = jobRepo.findAll();
        List<SemanticEmbedding> allJobEmbeddings = embeddingRepo.findAll().stream()
                .filter(e -> "JOB".equals(e.getEntityType()) && "nomic-embed-text".equals(e.getModelName()))
                .toList();

        List<SemanticSearchResult> results = new ArrayList<>();

        for (JobSemanticEnrichment job : allJobs) {
            if (!"COMPLETED".equalsIgnoreCase(job.getProcessingStatus())) continue;
            
            Optional<SemanticEmbedding> jobEmbOpt = allJobEmbeddings.stream()
                    .filter(e -> e.getEntityId().equals(job.getId()))
                    .findFirst();
                    
            if (jobEmbOpt.isPresent()) {
                double score = CosineSimilarityCalculator.calculate(queryVector, jobEmbOpt.get().getVector());
                results.add(new SemanticSearchResult(job, score));
            }
        }

        results.sort(Comparator.comparingDouble(SemanticSearchResult::getSimilarityScore).reversed());

        return results.size() > topK ? results.subList(0, topK) : results;
    }

    @Data
    public static class SemanticSearchResult {
        private final JobSemanticEnrichment job;
        private final double similarityScore;
    }
}
