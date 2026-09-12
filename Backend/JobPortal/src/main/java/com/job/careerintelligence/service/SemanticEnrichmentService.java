package com.job.careerintelligence.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.entity.RawJobObservation;
import com.job.careerintelligence.llm.LocalLlmClient;
import com.job.careerintelligence.llm.SemanticJobAnalysis;
import com.job.careerintelligence.repository.JobSemanticEnrichmentRepository;
import com.job.careerintelligence.repository.RawJobObservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SemanticEnrichmentService {

    private final LocalLlmClient llmClient;
    private final JobSemanticEnrichmentRepository enrichmentRepo;
    private final RawJobObservationRepository rawRepo;
    private final ObjectMapper objectMapper;
    private final SemanticAnalysisValidator validator;
    private final EmbeddingService embeddingService;

    @Transactional
    public List<JobSemanticEnrichment> processBatch(int limit) {
        java.util.List<JobSemanticEnrichment> newlyEnriched = new java.util.ArrayList<>();
        if (!llmClient.isModelAvailable()) {
            log.error("Ollama or model {} is not available. Aborting batch.", llmClient.getModelName());
            return newlyEnriched;
        }

        List<RawJobObservation> allRaw = rawRepo.findAllByOrderByCollectionTimestampDesc();
        int processed = 0;

        for (RawJobObservation raw : allRaw) {
            if (processed >= limit) break;

            JobSemanticEnrichment existing = enrichmentRepo.findByRawObservationId(raw.getId());
            if (existing != null && "COMPLETED".equals(existing.getProcessingStatus())) {
                continue;
            }

            JobSemanticEnrichment enriched = processObservation(raw, existing);
            if (enriched != null && "COMPLETED".equals(enriched.getProcessingStatus())) {
                newlyEnriched.add(enriched);
            }
            processed++;
        }
        return newlyEnriched;
    }

    @Transactional
    public List<JobSemanticEnrichment> processSpecificJobs(List<Long> rawJobIds) {
        java.util.List<JobSemanticEnrichment> newlyEnriched = new java.util.ArrayList<>();
        if (!llmClient.isModelAvailable()) {
            log.error("Ollama or model {} is not available. Aborting targeted processing.", llmClient.getModelName());
            return newlyEnriched;
        }

        for (Long rawId : rawJobIds) {
            java.util.Optional<RawJobObservation> rawOpt = rawRepo.findById(rawId);
            if (rawOpt.isEmpty()) continue;
            
            RawJobObservation raw = rawOpt.get();
            JobSemanticEnrichment existing = enrichmentRepo.findByRawObservationId(raw.getId());
            if (existing != null && "COMPLETED".equals(existing.getProcessingStatus())) {
                continue; // Already processed
            }

            JobSemanticEnrichment enriched = processObservation(raw, existing);
            if (enriched != null && "COMPLETED".equals(enriched.getProcessingStatus())) {
                newlyEnriched.add(enriched);
            }
        }
        return newlyEnriched;
    }

    @Transactional
    public JobSemanticEnrichment processObservation(RawJobObservation raw, JobSemanticEnrichment existing) {
        JobSemanticEnrichment enrichment = existing;
        if (enrichment == null) {
            enrichment = new JobSemanticEnrichment();
            enrichment.setRawObservationId(raw.getId());
        }

        enrichment.setModelName(llmClient.getModelName());
        enrichment.setProcessingTimestamp(LocalDateTime.now());
        enrichment.setProcessingStatus("PROCESSING");
        enrichment = enrichmentRepo.save(enrichment);

        try {
            // Build text context for the LLM
            StringBuilder context = new StringBuilder();
            context.append("TITLE: ").append(raw.getRawTitle()).append("\n");
            context.append("LOCATION: ").append(raw.getRawLocation()).append("\n");
            if (raw.getRawDepartment() != null) context.append("DEPARTMENT: ").append(raw.getRawDepartment()).append("\n");
            context.append("CONTENT:\n").append(raw.getRawHtml()).append("\n");

            SemanticJobAnalysis analysis = llmClient.analyzeJob(context.toString());

            if (analysis != null) {
                // Validate and sanitize the LLM output
                analysis = validator.validateAndSanitize(analysis, raw, llmClient.getModelName());
                enrichment.setCareerLevel(analysis.getCareerLevel());
                enrichment.setJobFamily(analysis.getJobFamily());
                enrichment.setSkills(toJson(analysis.getSkills()));
                enrichment.setPreferredSkills(toJson(analysis.getPreferredSkills()));
                enrichment.setExperienceYearsMin(analysis.getExperienceYearsMin());
                enrichment.setExperienceYearsMax(analysis.getExperienceYearsMax());
                enrichment.setEducation(analysis.getEducation());
                enrichment.setEmploymentType(analysis.getEmploymentType());
                enrichment.setRemoteStatus(analysis.getRemoteStatus());
                enrichment.setLocations(toJson(analysis.getLocations()));
                enrichment.setSalaryMin(analysis.getSalaryMin());
                enrichment.setSalaryMax(analysis.getSalaryMax());
                enrichment.setSalaryCurrency(analysis.getSalaryCurrency());
                enrichment.setSemanticSummary(analysis.getSemanticSummary());
                enrichment.setConfidence(analysis.getConfidence());
                enrichment.setAmbiguousFields(toJson(analysis.getAmbiguousFields()));
                
                enrichment.setProcessingStatus("COMPLETED");

                // Generate semantic text for embedding
                StringBuilder semText = new StringBuilder();
                semText.append(raw.getRawTitle() != null ? raw.getRawTitle() : "").append(" ");
                semText.append(analysis.getJobFamily() != null ? analysis.getJobFamily() : "").append(" ");
                semText.append(analysis.getCareerLevel() != null ? analysis.getCareerLevel() : "").append(" ");
                if (analysis.getSkills() != null) semText.append(String.join(", ", analysis.getSkills())).append(" ");
                if (analysis.getPreferredSkills() != null) semText.append(String.join(", ", analysis.getPreferredSkills())).append(" ");
                semText.append(analysis.getSemanticSummary() != null ? analysis.getSemanticSummary() : "");
                
                // We use raw observation ID as entity ID so the job enrichment corresponds 1:1 with the raw job.
                // Wait, entityId for Job is jobEnrichmentId to match SemanticSimilarityResult.
                enrichment = enrichmentRepo.save(enrichment);
                embeddingService.generateAndSaveEmbedding("JOB", enrichment.getId(), semText.toString().trim());
                
            } else {
                enrichment.setProcessingStatus("FAILED");
                enrichment = enrichmentRepo.save(enrichment);
            }

        } catch (Exception e) {
            log.error("Failed to process raw observation {}", raw.getId(), e);
            enrichment.setProcessingStatus("FAILED");
            enrichment = enrichmentRepo.save(enrichment);
        }

        return enrichment;
    }

    private String toJson(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return null;
        }
    }
}
