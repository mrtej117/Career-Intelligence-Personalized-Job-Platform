package com.job.careerintelligence.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.entity.RawJobObservation;
import com.job.careerintelligence.repository.JobSemanticEnrichmentRepository;
import com.job.careerintelligence.repository.RawJobObservationRepository;
import com.job.careerintelligence.repository.SemanticEmbeddingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingBackfillService {

    private final JobSemanticEnrichmentRepository enrichmentRepo;
    private final RawJobObservationRepository rawRepo;
    private final EmbeddingService embeddingService;
    private final SemanticEmbeddingRepository embeddingRepo;
    private final com.job.careerintelligence.llm.EmbeddingProvider embeddingProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public BackfillResult backfillJobEmbeddings() {
        log.info("Starting safe job embedding backfill...");
        List<JobSemanticEnrichment> completedJobs = enrichmentRepo.findByProcessingStatus("COMPLETED");

        int totalEvaluated = 0;
        int alreadyExists = 0;
        int generated = 0;
        long startTime = System.currentTimeMillis();

        String modelName = embeddingProvider.getModelName();

        for (JobSemanticEnrichment enrichment : completedJobs) {
            totalEvaluated++;
            
            boolean exists = embeddingRepo.findByEntityTypeAndEntityIdAndModelName("JOB", enrichment.getId(), modelName).isPresent();
            if (exists) {
                alreadyExists++;
                continue;
            }

            Optional<RawJobObservation> rawOpt = rawRepo.findById(enrichment.getRawObservationId());
            if (rawOpt.isEmpty()) {
                continue;
            }
            RawJobObservation raw = rawOpt.get();

            try {
                StringBuilder semText = new StringBuilder();
                semText.append(raw.getRawTitle() != null ? raw.getRawTitle() : "").append(" ");
                semText.append(enrichment.getJobFamily() != null ? enrichment.getJobFamily() : "").append(" ");
                semText.append(enrichment.getCareerLevel() != null ? enrichment.getCareerLevel() : "").append(" ");
                
                if (enrichment.getSkills() != null) {
                    try {
                        List<String> skillsList = objectMapper.readValue(enrichment.getSkills(), new TypeReference<List<String>>(){});
                        semText.append(String.join(", ", skillsList)).append(" ");
                    } catch (Exception e) {
                        semText.append(enrichment.getSkills()).append(" ");
                    }
                }
                
                if (enrichment.getPreferredSkills() != null) {
                    try {
                        List<String> prefSkillsList = objectMapper.readValue(enrichment.getPreferredSkills(), new TypeReference<List<String>>(){});
                        semText.append(String.join(", ", prefSkillsList)).append(" ");
                    } catch (Exception e) {
                        semText.append(enrichment.getPreferredSkills()).append(" ");
                    }
                }
                
                if (enrichment.getSemanticSummary() != null) {
                    semText.append(enrichment.getSemanticSummary());
                }

                embeddingService.generateAndSaveEmbedding("JOB", enrichment.getId(), semText.toString().trim());
                generated++;

            } catch (Exception e) {
                log.error("Failed to generate embedding for job {}", enrichment.getId(), e);
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Backfill complete in {}ms. Evaluated: {}, Already Existed: {}, Generated: {}", duration, totalEvaluated, alreadyExists, generated);
        
        return new BackfillResult(totalEvaluated, alreadyExists, generated, duration);
    }

    @Transactional
    public BackfillResult backfillSpecificJobEmbeddings(List<Long> enrichmentIds) {
        log.info("Starting targeted job embedding backfill for {} jobs...", enrichmentIds.size());

        int totalEvaluated = 0;
        int alreadyExists = 0;
        int generated = 0;
        long startTime = System.currentTimeMillis();

        String modelName = embeddingProvider.getModelName();

        for (Long enrichmentId : enrichmentIds) {
            Optional<JobSemanticEnrichment> enrichmentOpt = enrichmentRepo.findById(enrichmentId);
            if (enrichmentOpt.isEmpty() || !"COMPLETED".equals(enrichmentOpt.get().getProcessingStatus())) {
                continue;
            }
            JobSemanticEnrichment enrichment = enrichmentOpt.get();
            totalEvaluated++;
            
            boolean exists = embeddingRepo.findByEntityTypeAndEntityIdAndModelName("JOB", enrichment.getId(), modelName).isPresent();
            if (exists) {
                alreadyExists++;
                continue;
            }

            Optional<RawJobObservation> rawOpt = rawRepo.findById(enrichment.getRawObservationId());
            if (rawOpt.isEmpty()) {
                continue;
            }
            RawJobObservation raw = rawOpt.get();

            try {
                StringBuilder semText = new StringBuilder();
                semText.append(raw.getRawTitle() != null ? raw.getRawTitle() : "").append(" ");
                semText.append(enrichment.getJobFamily() != null ? enrichment.getJobFamily() : "").append(" ");
                semText.append(enrichment.getCareerLevel() != null ? enrichment.getCareerLevel() : "").append(" ");
                
                if (enrichment.getSkills() != null) {
                    try {
                        List<String> skillsList = objectMapper.readValue(enrichment.getSkills(), new TypeReference<List<String>>(){});
                        semText.append(String.join(", ", skillsList)).append(" ");
                    } catch (Exception e) {
                        semText.append(enrichment.getSkills()).append(" ");
                    }
                }
                
                if (enrichment.getPreferredSkills() != null) {
                    try {
                        List<String> prefSkillsList = objectMapper.readValue(enrichment.getPreferredSkills(), new TypeReference<List<String>>(){});
                        semText.append(String.join(", ", prefSkillsList)).append(" ");
                    } catch (Exception e) {
                        semText.append(enrichment.getPreferredSkills()).append(" ");
                    }
                }
                
                if (enrichment.getSemanticSummary() != null) {
                    semText.append(enrichment.getSemanticSummary());
                }

                embeddingService.generateAndSaveEmbedding("JOB", enrichment.getId(), semText.toString().trim());
                generated++;

            } catch (Exception e) {
                log.error("Failed to generate embedding for targeted job {}", enrichment.getId(), e);
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Targeted backfill complete in {}ms. Evaluated: {}, Already Existed: {}, Generated: {}", duration, totalEvaluated, alreadyExists, generated);
        
        return new BackfillResult(totalEvaluated, alreadyExists, generated, duration);
    }

    public static class BackfillResult {
        public int totalEvaluated;
        public int alreadyExists;
        public int generated;
        public long durationMs;

        public BackfillResult(int totalEvaluated, int alreadyExists, int generated, long durationMs) {
            this.totalEvaluated = totalEvaluated;
            this.alreadyExists = alreadyExists;
            this.generated = generated;
            this.durationMs = durationMs;
        }
    }
}
