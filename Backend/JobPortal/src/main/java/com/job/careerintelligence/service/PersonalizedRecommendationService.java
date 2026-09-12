package com.job.careerintelligence.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.dto.RecommendationDTO;
import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalizedRecommendationService {

    private final JobMatchResultRepository jobMatchResultRepo;
    private final CandidateSemanticProfileRepository candidateProfileRepo;
    private final JobSemanticEnrichmentRepository jobEnrichmentRepo;
    private final RawJobObservationRepository rawObservationRepo;
    private final com.job.repository.JobRepository jobRepoInternal;
    private final com.job.careerintelligence.repository.HybridMatchResultRepository hybridRepo;
    private final AdaptiveRecommendationService adaptiveRecommendationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public Page<RecommendationDTO> getRecommendationsForSeeker(Long jobSeekerId, Pageable pageable) {
        log.info("Fetching recommendations for jobSeekerId: {}", jobSeekerId);

        Optional<CandidateSemanticProfile> latestProfileOpt = candidateProfileRepo
                .findTopByJobSeekerIdAndIsLatestVersionTrueOrderByProcessingTimestampDesc(jobSeekerId);

        if (latestProfileOpt.isEmpty()) {
            log.warn("No active CandidateSemanticProfile found for jobSeekerId: {}", jobSeekerId);
            return Page.empty(pageable);
        }

        CandidateSemanticProfile profile = latestProfileOpt.get();

        List<HybridMatchResult> hybridMatches = hybridRepo.findByCandidateProfileIdOrderByHybridScoreDesc(profile.getId());
        List<RecommendationDTO> dtoList;

        if (!hybridMatches.isEmpty()) {
            dtoList = hybridMatches.stream().map(hybrid -> {
                Optional<JobMatchResult> detOpt = jobMatchResultRepo.findByCandidateProfileIdAndJobEnrichmentId(profile.getId(), hybrid.getJobEnrichmentId());
                return mapToDTO(detOpt.orElse(null), hybrid);
            }).collect(java.util.stream.Collectors.toList());
        } else {
            List<JobMatchResult> detMatches = jobMatchResultRepo.findByCandidateProfileIdOrderByOverallScoreDesc(profile.getId());
            dtoList = detMatches.stream().map(this::mapDetToDTO).collect(java.util.stream.Collectors.toList());
        }

        // Apply Stage 6 Adaptive Re-Ranking at the recommendation-ranking layer
        adaptiveRecommendationService.applyAdaptiveRanking(jobSeekerId, dtoList);

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtoList.size());
        List<RecommendationDTO> pageContent = (start < dtoList.size()) ? dtoList.subList(start, end) : Collections.emptyList();

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, dtoList.size());
    }

    private RecommendationDTO mapDetToDTO(JobMatchResult detMatch) {
        RecommendationDTO dto = new RecommendationDTO();
        dto.setMatchResultId(detMatch.getId());
        dto.setJobEnrichmentId(detMatch.getJobEnrichmentId());
        dto.setOverallScore(detMatch.getOverallScore());
        dto.setDeterministicScore(detMatch.getOverallScore());
        dto.setSemanticScore(0.0);
        dto.setHybridScore(detMatch.getOverallScore());
        dto.setConfidence(detMatch.getConfidence());
        dto.setShortExplanation(detMatch.getExplanation());

        try {
            if (detMatch.getMatchedSkillsJson() != null) {
                dto.setMatchedSkills(objectMapper.readValue(detMatch.getMatchedSkillsJson(), new TypeReference<List<String>>(){}));
            }
            if (detMatch.getMissingSkillsJson() != null) {
                dto.setMissingSkills(objectMapper.readValue(detMatch.getMissingSkillsJson(), new TypeReference<List<String>>(){}));
            }
            if (detMatch.getStrengthsJson() != null) {
                dto.setStrengths(objectMapper.readValue(detMatch.getStrengthsJson(), new TypeReference<List<String>>(){}));
            }
            if (detMatch.getGapsJson() != null) {
                dto.setGaps(objectMapper.readValue(detMatch.getGapsJson(), new TypeReference<List<String>>(){}));
            }
        } catch (Exception e) {
            log.error("Failed to parse skills/strengths JSON in mapDetToDTO", e);
        }

        populateEnrichmentDetails(dto, detMatch.getJobEnrichmentId());
        return dto;
    }

    private RecommendationDTO mapToDTO(JobMatchResult detMatch, HybridMatchResult hybrid) {
        RecommendationDTO dto = new RecommendationDTO();
        dto.setMatchResultId(hybrid.getId());
        dto.setJobEnrichmentId(hybrid.getJobEnrichmentId());

        dto.setOverallScore(hybrid.getDeterministicScore());
        dto.setSemanticScore(hybrid.getSemanticScore());
        dto.setDeterministicScore(hybrid.getDeterministicScore());
        dto.setHybridScore(hybrid.getHybridScore());
        dto.setConfidence(hybrid.getConfidence()); 
        if (detMatch != null) {
            dto.setShortExplanation(detMatch.getExplanation());
        }
        
        try {
            if (hybrid.getMatchedSkills() != null) {
                dto.setMatchedSkills(objectMapper.readValue(hybrid.getMatchedSkills(), new TypeReference<List<String>>(){}));
            }
            if (hybrid.getMissingSkills() != null) {
                dto.setMissingSkills(objectMapper.readValue(hybrid.getMissingSkills(), new TypeReference<List<String>>(){}));
            }
            if (hybrid.getStrengths() != null) {
                dto.setStrengths(objectMapper.readValue(hybrid.getStrengths(), new TypeReference<List<String>>(){}));
            }
            if (hybrid.getGaps() != null) {
                dto.setGaps(objectMapper.readValue(hybrid.getGaps(), new TypeReference<List<String>>(){}));
            }
        } catch (Exception e) {
            log.error("Failed to parse skills/strengths JSON", e);
        }

        populateEnrichmentDetails(dto, hybrid.getJobEnrichmentId());
        return dto;
    }

    private void populateEnrichmentDetails(RecommendationDTO dto, Long jobEnrichmentId) {
        if (jobEnrichmentId == null) return;
        jobEnrichmentRepo.findById(jobEnrichmentId).ifPresent(enrichment -> {
            dto.setRawObservationId(enrichment.getRawObservationId());
            dto.setEmploymentType(enrichment.getEmploymentType());
            dto.setWorkMode(enrichment.getRemoteStatus());
            
            rawObservationRepo.findById(enrichment.getRawObservationId()).ifPresent(raw -> {
                dto.setJobTitle(raw.getRawTitle());
                dto.setTitle(raw.getRawTitle());
                dto.setLocation(raw.getRawLocation());
                dto.setSourceJobUrl(raw.getSourceJobUrl());
                
                List<com.job.entity.Job> matchingJobs = jobRepoInternal.findFirstByTitleAndCompanyName(
                    raw.getRawTitle(), 
                    raw.getJobIdentity() != null ? raw.getJobIdentity().getCompanyName() : ""
                );
                if (!matchingJobs.isEmpty()) {
                    dto.setId(matchingJobs.get(0).getId());
                }
                
                if (raw.getJobIdentity() != null) {
                    dto.setCompanyName(raw.getJobIdentity().getCompanyName());
                    String safeName = raw.getJobIdentity().getCompanyName().toLowerCase().replaceAll("\\s+", "");
                    String logo = "https://www.google.com/s2/favicons?domain=" + safeName + ".com&sz=128";
                    dto.setCompanyLogoUrl(logo);
                    dto.setProfilePicture(logo);
                }
            });
        });
    }
}
