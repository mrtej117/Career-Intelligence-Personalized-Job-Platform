package com.job.careerintelligence.service;

import com.job.careerintelligence.entity.HybridMatchResult;
import com.job.careerintelligence.entity.JobMatchResult;
import com.job.careerintelligence.entity.SemanticSimilarityResult;
import com.job.careerintelligence.repository.HybridMatchResultRepository;
import com.job.careerintelligence.repository.JobMatchResultRepository;
import com.job.careerintelligence.repository.SemanticSimilarityResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HybridRecommendationService {

    private final JobMatchResultRepository matchResultRepo;
    private final SemanticSimilarityResultRepository semanticResultRepo;
    private final HybridMatchResultRepository hybridRepo;

    @Value("${career.intelligence.hybrid.deterministic-weight:0.80}")
    private double deterministicWeight;

    @Value("${career.intelligence.hybrid.semantic-weight:0.20}")
    private double semanticWeight;

    @Transactional
    public HybridMatchResult calculateAndStoreHybridScore(Long candidateProfileId, Long jobEnrichmentId) {
        // Validate weights
        if (Math.abs((deterministicWeight + semanticWeight) - 1.0) > 0.001) {
            log.warn("Hybrid weights do not sum to 1.0! Deterministic: {}, Semantic: {}", deterministicWeight, semanticWeight);
            // Default fallback safely
            deterministicWeight = 0.80;
            semanticWeight = 0.20;
        }

        // Fetch deterministic score
        Optional<JobMatchResult> detResultOpt = matchResultRepo.findByCandidateProfileIdAndJobEnrichmentId(candidateProfileId, jobEnrichmentId);
        double detScore = 0.0;
        String matchedSkills = "";
        String missingSkills = "";
        String strengths = "";
        String gaps = "";
        String confidence = "LOW";

        if (detResultOpt.isPresent()) {
            JobMatchResult det = detResultOpt.get();
            detScore = det.getOverallScore();
            matchedSkills = det.getMatchedSkillsJson();
            missingSkills = det.getMissingSkillsJson();
            strengths = det.getStrengthsJson();
            gaps = det.getGapsJson();
            confidence = det.getConfidence() != null ? det.getConfidence() : "LOW";
        }

        // Fetch semantic score
        Optional<SemanticSimilarityResult> semResultOpt = semanticResultRepo.findByCandidateProfileIdAndJobEnrichmentIdAndModelName(candidateProfileId, jobEnrichmentId, "nomic-embed-text");
        double semScore = 0.0;
        if (semResultOpt.isPresent()) {
            semScore = semResultOpt.get().getSimilarityScore();
        }

        // Calculate Hybrid
        double hybridScore = (detScore * deterministicWeight) + (semScore * semanticWeight);
        
        // Ensure bounds
        hybridScore = Math.max(0.0, Math.min(100.0, hybridScore));

        // Save
        Optional<HybridMatchResult> existingOpt = hybridRepo.findByCandidateProfileIdAndJobEnrichmentId(candidateProfileId, jobEnrichmentId);
        HybridMatchResult hybrid = existingOpt.orElse(new HybridMatchResult());
        hybrid.setCandidateProfileId(candidateProfileId);
        hybrid.setJobEnrichmentId(jobEnrichmentId);
        hybrid.setDeterministicScore(detScore);
        hybrid.setSemanticScore(semScore);
        hybrid.setHybridScore(hybridScore);
        hybrid.setMatchedSkills(matchedSkills);
        hybrid.setMissingSkills(missingSkills);
        hybrid.setStrengths(strengths);
        hybrid.setGaps(gaps);
        hybrid.setConfidence(confidence);

        return hybridRepo.save(hybrid);
    }
}
