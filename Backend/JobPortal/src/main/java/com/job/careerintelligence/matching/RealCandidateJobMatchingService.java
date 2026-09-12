package com.job.careerintelligence.matching;

import com.job.careerintelligence.entity.CandidateSemanticProfile;
import com.job.careerintelligence.entity.JobMatchResult;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.repository.CandidateSemanticProfileRepository;
import com.job.careerintelligence.repository.JobSemanticEnrichmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to connect real CandidateSemanticProfiles to JobSemanticEnrichment records
 * and batch process them through the JobMatchingService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RealCandidateJobMatchingService {

    private final CandidateSemanticProfileRepository candidateRepo;
    private final JobSemanticEnrichmentRepository jobRepo;
    private final JobMatchingService jobMatchingService;
    private final com.job.careerintelligence.repository.SemanticEmbeddingRepository embeddingRepo;
    private final com.job.careerintelligence.repository.SemanticSimilarityResultRepository semanticResultRepo;
    private final com.job.careerintelligence.llm.EmbeddingProvider embeddingProvider;
    private final com.job.careerintelligence.service.HybridRecommendationService hybridService;

    public BatchMatchResult matchCandidateToAllJobs(Long candidateProfileId) {
        log.info("Starting batch match for candidate profile id: {}", candidateProfileId);
        long startTime = System.currentTimeMillis();

        CandidateSemanticProfile candidate = candidateRepo.findById(candidateProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate profile not found"));

        // Fetch all COMPLETED jobs
        List<JobSemanticEnrichment> jobs = jobRepo.findByProcessingStatus("COMPLETED");
        List<JobMatchResult> results = new ArrayList<>();

        int successful = 0;
        int failed = 0;
        long fastest = Long.MAX_VALUE;
        long slowest = 0;

        for (JobSemanticEnrichment job : jobs) {
            if (!"COMPLETED".equalsIgnoreCase(job.getProcessingStatus())) {
                continue; // Skip incomplete enrichments
            }
            long matchStart = System.currentTimeMillis();
            try {
                JobMatchResult result = jobMatchingService.calculateMatch(candidate, job);
                calculateAndStoreSemanticSimilarity(candidate, job);
                hybridService.calculateAndStoreHybridScore(candidate.getId(), job.getId());
                results.add(result);
                successful++;
                long duration = System.currentTimeMillis() - matchStart;
                if (duration < fastest) fastest = duration;
                if (duration > slowest) slowest = duration;
            } catch (Exception e) {
                log.error("Failed to match candidate {} to job {}: {}", candidateProfileId, job.getId(), e.getMessage());
                failed++;
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;
        long avgTime = successful > 0 ? totalTime / successful : 0;
        if (fastest == Long.MAX_VALUE) fastest = 0;

        BatchMatchResult summary = new BatchMatchResult();
        summary.setCandidateProfileId(candidateProfileId);
        summary.setTotalJobsProcessed(successful + failed);
        summary.setSuccessfulMatches(successful);
        summary.setFailedMatches(failed);
        summary.setTotalTimeMs(totalTime);
        summary.setAverageTimeMs(avgTime);
        summary.setFastestMatchMs(fastest);
        summary.setSlowestMatchMs(slowest);
        summary.setResults(results);

        log.info("Batch match completed. Processed: {}, Successful: {}, Failed: {}, Total Time: {}ms, Avg Time: {}ms",
                summary.getTotalJobsProcessed(), successful, failed, totalTime, avgTime);

        return summary;
    }
    
    public BatchMatchResult matchAllCandidatesToAllJobs() {
        log.info("Starting universal batch match");
        long startTime = System.currentTimeMillis();

        List<CandidateSemanticProfile> candidates = candidateRepo.findAll();
        List<JobSemanticEnrichment> jobs = jobRepo.findByProcessingStatus("COMPLETED");
        List<JobMatchResult> results = new ArrayList<>();

        int successful = 0;
        int failed = 0;
        long fastest = Long.MAX_VALUE;
        long slowest = 0;

        for(CandidateSemanticProfile candidate : candidates) {
            if (!Boolean.TRUE.equals(candidate.getIsLatestVersion()) || !"COMPLETED".equalsIgnoreCase(candidate.getProcessingStatus())) {
                continue;
            }
            for (JobSemanticEnrichment job : jobs) {
                if (!"COMPLETED".equalsIgnoreCase(job.getProcessingStatus())) {
                    continue; 
                }
                long matchStart = System.currentTimeMillis();
                try {
                    JobMatchResult result = jobMatchingService.calculateMatch(candidate, job);
                calculateAndStoreSemanticSimilarity(candidate, job);
                hybridService.calculateAndStoreHybridScore(candidate.getId(), job.getId());
                    results.add(result);
                    successful++;
                    long duration = System.currentTimeMillis() - matchStart;
                    if (duration < fastest) fastest = duration;
                    if (duration > slowest) slowest = duration;
                } catch (Exception e) {
                    log.error("Failed to match candidate {} to job {}: {}", candidate.getId(), job.getId(), e.getMessage());
                    failed++;
                }
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;
        long avgTime = successful > 0 ? totalTime / successful : 0;
        if (fastest == Long.MAX_VALUE) fastest = 0;

        BatchMatchResult summary = new BatchMatchResult();
        summary.setTotalJobsProcessed(successful + failed);
        summary.setSuccessfulMatches(successful);
        summary.setFailedMatches(failed);
        summary.setTotalTimeMs(totalTime);
        summary.setAverageTimeMs(avgTime);
        summary.setFastestMatchMs(fastest);
        summary.setSlowestMatchMs(slowest);
        summary.setResults(results);

        log.info("Universal batch match completed. Candidates: {}, Jobs: {}, Successful: {}, Failed: {}, Total Time: {}ms, Avg Time: {}ms",
                candidates.size(), jobs.size(), successful, failed, totalTime, avgTime);

        return summary;
    }
    
    public BatchMatchResult matchSpecificJobsToAllCandidates(List<JobSemanticEnrichment> specificJobs) {
        log.info("Starting targeted batch match for {} jobs", specificJobs.size());
        long startTime = System.currentTimeMillis();

        List<CandidateSemanticProfile> candidates = candidateRepo.findAll();
        List<JobMatchResult> results = new ArrayList<>();

        int successful = 0;
        int failed = 0;
        long fastest = Long.MAX_VALUE;
        long slowest = 0;

        for(CandidateSemanticProfile candidate : candidates) {
            if (!Boolean.TRUE.equals(candidate.getIsLatestVersion()) || !"COMPLETED".equalsIgnoreCase(candidate.getProcessingStatus())) {
                continue;
            }
            for (JobSemanticEnrichment job : specificJobs) {
                if (!"COMPLETED".equalsIgnoreCase(job.getProcessingStatus())) {
                    continue; 
                }
                long matchStart = System.currentTimeMillis();
                try {
                    JobMatchResult result = jobMatchingService.calculateMatch(candidate, job);
                calculateAndStoreSemanticSimilarity(candidate, job);
                hybridService.calculateAndStoreHybridScore(candidate.getId(), job.getId());
                    results.add(result);
                    successful++;
                    long duration = System.currentTimeMillis() - matchStart;
                    if (duration < fastest) fastest = duration;
                    if (duration > slowest) slowest = duration;
                } catch (Exception e) {
                    log.error("Failed to match candidate {} to job {}: {}", candidate.getId(), job.getId(), e.getMessage());
                    failed++;
                }
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;
        long avgTime = successful > 0 ? totalTime / successful : 0;
        if (fastest == Long.MAX_VALUE) fastest = 0;

        BatchMatchResult summary = new BatchMatchResult();
        summary.setCandidateProfileId(null);
        summary.setTotalJobsProcessed(successful + failed);
        summary.setSuccessfulMatches(successful);
        summary.setFailedMatches(failed);
        summary.setTotalTimeMs(totalTime);
        summary.setAverageTimeMs(avgTime);
        summary.setFastestMatchMs(fastest);
        summary.setSlowestMatchMs(slowest);
        summary.setResults(results);

        log.info("Targeted batch match completed. Candidates: {}, Jobs: {}, Successful: {}, Failed: {}, Total Time: {}ms, Avg Time: {}ms",
                candidates.size(), specificJobs.size(), successful, failed, totalTime, avgTime);

        return summary;
    }

    private void calculateAndStoreSemanticSimilarity(CandidateSemanticProfile candidate, JobSemanticEnrichment job) {
        String modelName = embeddingProvider.getModelName();
        java.util.Optional<com.job.careerintelligence.entity.SemanticEmbedding> candEmbOpt = embeddingRepo.findByEntityTypeAndEntityIdAndModelName("CANDIDATE", candidate.getId(), modelName);
        java.util.Optional<com.job.careerintelligence.entity.SemanticEmbedding> jobEmbOpt = embeddingRepo.findByEntityTypeAndEntityIdAndModelName("JOB", job.getId(), modelName);

        if (candEmbOpt.isPresent() && jobEmbOpt.isPresent()) {
            double[] candVector = candEmbOpt.get().getVector();
            double[] jobVector = jobEmbOpt.get().getVector();
            
            double score = com.job.careerintelligence.util.CosineSimilarityCalculator.calculate(candVector, jobVector);
            
            java.util.Optional<com.job.careerintelligence.entity.SemanticSimilarityResult> existingResult = 
                    semanticResultRepo.findByCandidateProfileIdAndJobEnrichmentIdAndModelName(candidate.getId(), job.getId(), modelName);
                    
            com.job.careerintelligence.entity.SemanticSimilarityResult res = existingResult.orElse(new com.job.careerintelligence.entity.SemanticSimilarityResult());
            res.setCandidateProfileId(candidate.getId());
            res.setJobEnrichmentId(job.getId());
            res.setCandidateEmbeddingId(candEmbOpt.get().getId());
            res.setJobEmbeddingId(jobEmbOpt.get().getId());
            res.setModelName(modelName);
            res.setSimilarityScore(score);
            semanticResultRepo.save(res);
        }
    }

    @lombok.Data
    public static class BatchMatchResult {
        private Long candidateProfileId;
        private int totalJobsProcessed;
        private int successfulMatches;
        private int failedMatches;
        private long totalTimeMs;
        private long averageTimeMs;
        private long fastestMatchMs;
        private long slowestMatchMs;
        private List<JobMatchResult> results;
    }
}
