package com.job.careerintelligence.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.dto.CandidateActionResponseDTO;
import com.job.careerintelligence.dto.CandidatePreferenceProfileDTO;
import com.job.careerintelligence.dto.RecommendationDTO;
import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.repository.*;
import com.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdaptiveRecommendationService {

    private final CandidateActionRepository actionRepo;
    private final CandidatePreferenceProfileRepository preferenceRepo;
    private final JobSemanticEnrichmentRepository enrichmentRepo;
    private final RawJobObservationRepository rawJobRepo;
    private final JobRepository internalJobRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public CandidateActionResponseDTO recordAction(Long candidateId, Long jobId, Long jobEnrichmentId, CandidateActionType actionType, String metadata) {
        log.info("Recording action {} for candidateId: {}, jobId: {}, enrichmentId: {}", actionType, candidateId, jobId, jobEnrichmentId);

        // 1. Resolve job attributes
        ResolvedJobSignals signals = resolveJobSignals(jobId, jobEnrichmentId);

        // 2. Persist CandidateAction
        CandidateAction action = new CandidateAction();
        action.setCandidateId(candidateId);
        action.setJobId(signals.jobId);
        action.setJobEnrichmentId(signals.jobEnrichmentId);
        action.setActionType(actionType);
        action.setTimestamp(LocalDateTime.now());
        action.setJobFamily(signals.jobFamily);
        action.setCompanyName(signals.companyName);
        action.setWorkMode(signals.workMode);
        action.setLocation(signals.location);
        try {
            action.setSkillsJson(objectMapper.writeValueAsString(signals.skills));
        } catch (Exception e) {
            action.setSkillsJson("[]");
        }
        action.setMetadataJson(metadata);
        action = actionRepo.save(action);

        // 3. Update CandidatePreferenceProfile
        CandidatePreferenceProfile profile = updatePreferenceProfile(candidateId, signals, actionType);

        return CandidateActionResponseDTO.builder()
                .actionId(action.getId())
                .candidateId(candidateId)
                .jobId(signals.jobId)
                .jobEnrichmentId(signals.jobEnrichmentId)
                .actionType(actionType)
                .timestamp(action.getTimestamp())
                .updatedInteractionCount(profile.getInteractionCount())
                .summary(profile.getSummary())
                .build();
    }

    @Transactional(readOnly = true)
    public Optional<CandidatePreferenceProfile> getPreferenceProfile(Long candidateId) {
        return preferenceRepo.findByCandidateId(candidateId);
    }

    @Transactional(readOnly = true)
    public CandidatePreferenceProfileDTO getPreferenceProfileDTO(Long candidateId) {
        Optional<CandidatePreferenceProfile> opt = preferenceRepo.findByCandidateId(candidateId);
        if (opt.isEmpty()) {
            return CandidatePreferenceProfileDTO.builder()
                    .candidateId(candidateId)
                    .lastUpdated(LocalDateTime.now())
                    .interactionCount(0)
                    .preferredJobFamilies(Collections.emptyMap())
                    .preferredSkills(Collections.emptyMap())
                    .preferredWorkModes(Collections.emptyMap())
                    .preferredLocations(Collections.emptyMap())
                    .preferredCompanies(Collections.emptyMap())
                    .rejectedJobIds(Collections.emptySet())
                    .summary("No candidate actions recorded yet. Recommendations use baseline 80/20 hybrid scoring.")
                    .build();
        }

        CandidatePreferenceProfile p = opt.get();
        return CandidatePreferenceProfileDTO.builder()
                .candidateId(candidateId)
                .lastUpdated(p.getLastUpdated())
                .interactionCount(p.getInteractionCount())
                .preferredJobFamilies(readMap(p.getPreferredJobFamiliesJson()))
                .preferredSkills(readMap(p.getPreferredSkillsJson()))
                .preferredWorkModes(readMap(p.getPreferredWorkModesJson()))
                .preferredLocations(readMap(p.getPreferredLocationsJson()))
                .preferredCompanies(readMap(p.getPreferredCompaniesJson()))
                .rejectedJobIds(readSet(p.getRejectedJobIdsJson()))
                .summary(p.getSummary())
                .build();
    }

    /**
     * Applies adaptive re-ranking to candidate recommendations at the ranking layer.
     * Underlying hybridScore, deterministicScore, and semanticScore remain 100% untouched.
     */
    public void applyAdaptiveRanking(Long candidateId, List<RecommendationDTO> recommendations) {
        if (recommendations == null || recommendations.isEmpty()) {
            return;
        }

        Optional<CandidatePreferenceProfile> profileOpt = preferenceRepo.findByCandidateId(candidateId);
        if (profileOpt.isEmpty() || profileOpt.get().getInteractionCount() == 0) {
            // Cold-start: preserve exact Stage 5 hybrid order
            for (RecommendationDTO dto : recommendations) {
                dto.setPreferenceScore(null);
                dto.setAdaptiveScore(dto.getHybridScore());
            }
            return;
        }

        CandidatePreferenceProfile profile = profileOpt.get();
        Map<String, Double> preferredFamilies = readMap(profile.getPreferredJobFamiliesJson());
        Map<String, Double> preferredSkills = readMap(profile.getPreferredSkillsJson());
        Map<String, Double> preferredWorkModes = readMap(profile.getPreferredWorkModesJson());
        Map<String, Double> preferredLocations = readMap(profile.getPreferredLocationsJson());
        Map<String, Double> preferredCompanies = readMap(profile.getPreferredCompaniesJson());
        Set<Long> rejectedJobIds = readSet(profile.getRejectedJobIdsJson());

        // Adaptive weight scales gently with interaction count: 5% for 1 action, up to 25% max
        double adaptiveWeight = Math.min(0.25, profile.getInteractionCount() * 0.05);

        for (RecommendationDTO dto : recommendations) {
            double preferenceScore = calculatePreferenceScoreForDTO(
                    dto, preferredFamilies, preferredSkills, preferredWorkModes,
                    preferredLocations, preferredCompanies, rejectedJobIds
            );

            double baseHybrid = dto.getHybridScore() != null ? dto.getHybridScore() : 0.0;
            double combinedAdaptive = ((1.0 - adaptiveWeight) * baseHybrid) + (adaptiveWeight * preferenceScore);
            combinedAdaptive = Math.max(0.0, Math.min(100.0, combinedAdaptive));

            dto.setPreferenceScore(Math.round(preferenceScore * 10.0) / 10.0);
            dto.setAdaptiveScore(Math.round(combinedAdaptive * 10.0) / 10.0);
        }

        // Rank by adaptive score descending
        recommendations.sort((a, b) -> {
            double scoreA = a.getAdaptiveScore() != null ? a.getAdaptiveScore() : 0.0;
            double scoreB = b.getAdaptiveScore() != null ? b.getAdaptiveScore() : 0.0;
            return Double.compare(scoreB, scoreA);
        });
    }

    public double calculatePreferenceScoreForDTO(
            RecommendationDTO dto,
            Map<String, Double> preferredFamilies,
            Map<String, Double> preferredSkills,
            Map<String, Double> preferredWorkModes,
            Map<String, Double> preferredLocations,
            Map<String, Double> preferredCompanies,
            Set<Long> rejectedJobIds) {

        // Dimensional scores normalized 0 to 100
        double familyScore = scoreDimension(dto.getTitle(), preferredFamilies);
        double workModeScore = scoreDimension(dto.getWorkMode(), preferredWorkModes);
        double locationScore = scoreDimension(dto.getLocation(), preferredLocations);
        double companyScore = scoreDimension(dto.getCompanyName(), preferredCompanies);

        // Skills scoring based on matchedSkills overlap
        double skillsScore = 50.0;
        if (dto.getMatchedSkills() != null && !dto.getMatchedSkills().isEmpty() && !preferredSkills.isEmpty()) {
            double totalSkillWeight = 0.0;
            int evaluatedSkills = 0;
            for (String skill : dto.getMatchedSkills()) {
                Double weight = preferredSkills.get(skill.toLowerCase());
                if (weight != null) {
                    totalSkillWeight += weight;
                    evaluatedSkills++;
                }
            }
            if (evaluatedSkills > 0) {
                // scale average skill weight into [0..100] with 50 as neutral
                skillsScore = Math.max(0.0, Math.min(100.0, 50.0 + (totalSkillWeight * 10.0)));
            }
        }

        // Weighted combination: Skills (35%), Family/Title (25%), WorkMode (20%), Company (10%), Location (10%)
        double rawPreference = (skillsScore * 0.35)
                + (familyScore * 0.25)
                + (workModeScore * 0.20)
                + (companyScore * 0.10)
                + (locationScore * 0.10);

        // Constraint 2: Deterministic negative signal for rejected job.
        // Penalty applied without hard-filtering from future recommendations.
        if (rejectedJobIds != null) {
            boolean isRejected = (dto.getId() != null && rejectedJobIds.contains(dto.getId()))
                    || (dto.getJobEnrichmentId() != null && rejectedJobIds.contains(dto.getJobEnrichmentId()))
                    || (dto.getRawObservationId() != null && rejectedJobIds.contains(dto.getRawObservationId()));
            if (isRejected) {
                rawPreference = Math.max(5.0, rawPreference * 0.35); // 65% reduction
            }
        }

        return Math.max(0.0, Math.min(100.0, rawPreference));
    }

    private double scoreDimension(String value, Map<String, Double> preferences) {
        if (value == null || value.isBlank() || preferences == null || preferences.isEmpty()) {
            return 50.0; // neutral
        }
        String cleanVal = value.trim().toLowerCase();
        for (Map.Entry<String, Double> entry : preferences.entrySet()) {
            String key = entry.getKey().toLowerCase();
            if (cleanVal.contains(key) || key.contains(cleanVal)) {
                double weight = entry.getValue();
                return Math.max(0.0, Math.min(100.0, 50.0 + (weight * 10.0)));
            }
        }
        return 50.0;
    }

    private CandidatePreferenceProfile updatePreferenceProfile(Long candidateId, ResolvedJobSignals signals, CandidateActionType actionType) {
        CandidatePreferenceProfile profile = preferenceRepo.findByCandidateId(candidateId)
                .orElseGet(() -> {
                    CandidatePreferenceProfile newP = new CandidatePreferenceProfile();
                    newP.setCandidateId(candidateId);
                    newP.setInteractionCount(0);
                    return newP;
                });

        double weight = actionType.getWeight();
        Map<String, Double> families = readMap(profile.getPreferredJobFamiliesJson());
        Map<String, Double> skills = readMap(profile.getPreferredSkillsJson());
        Map<String, Double> workModes = readMap(profile.getPreferredWorkModesJson());
        Map<String, Double> locations = readMap(profile.getPreferredLocationsJson());
        Map<String, Double> companies = readMap(profile.getPreferredCompaniesJson());
        Set<Long> rejectedJobIds = readSet(profile.getRejectedJobIdsJson());

        // Update Job Family
        if (signals.jobFamily != null && !signals.jobFamily.isBlank()) {
            families.merge(signals.jobFamily, weight, Double::sum);
        }

        // Update Company
        if (signals.companyName != null && !signals.companyName.isBlank()) {
            companies.merge(signals.companyName, weight, Double::sum);
        }

        // Update Work Mode
        if (signals.workMode != null && !signals.workMode.isBlank()) {
            workModes.merge(signals.workMode, weight, Double::sum);
        }

        // Update Location
        if (signals.location != null && !signals.location.isBlank()) {
            locations.merge(signals.location, weight, Double::sum);
        }

        // Update Skills (each skill receives half the action weight)
        if (signals.skills != null) {
            for (String skill : signals.skills) {
                if (skill != null && !skill.isBlank()) {
                    skills.merge(skill.trim().toLowerCase(), weight * 0.5, Double::sum);
                }
            }
        }

        // Record rejected job ID if action is REJECT
        if (actionType == CandidateActionType.REJECT) {
            if (signals.jobId != null) rejectedJobIds.add(signals.jobId);
            if (signals.jobEnrichmentId != null) rejectedJobIds.add(signals.jobEnrichmentId);
        }

        profile.setInteractionCount(profile.getInteractionCount() + 1);
        profile.setLastUpdated(LocalDateTime.now());
        profile.setPreferredJobFamiliesJson(writeJson(families));
        profile.setPreferredSkillsJson(writeJson(skills));
        profile.setPreferredWorkModesJson(writeJson(workModes));
        profile.setPreferredLocationsJson(writeJson(locations));
        profile.setPreferredCompaniesJson(writeJson(companies));
        profile.setRejectedJobIdsJson(writeJson(rejectedJobIds));

        // Generate explainable summary
        profile.setSummary(buildExplainableSummary(profile.getInteractionCount(), families, skills, workModes, companies));

        return preferenceRepo.save(profile);
    }

    private String buildExplainableSummary(int count, Map<String, Double> families, Map<String, Double> skills, Map<String, Double> modes, Map<String, Double> companies) {
        List<String> topSkills = skills.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(4)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<String> topFamilies = families.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        String topMode = modes.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        String topCompany = companies.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Learned from %d interactions. ", count));
        if (!topSkills.isEmpty()) {
            sb.append("Top skills: ").append(topSkills).append(". ");
        }
        if (!topFamilies.isEmpty()) {
            sb.append("Preferred fields: ").append(topFamilies).append(". ");
        }
        if (topMode != null) {
            sb.append("Preferred work mode: ").append(topMode).append(". ");
        }
        if (topCompany != null) {
            sb.append("Preferred company: ").append(topCompany).append(". ");
        }
        return sb.toString().trim();
    }

    private ResolvedJobSignals resolveJobSignals(Long jobId, Long jobEnrichmentId) {
        ResolvedJobSignals s = new ResolvedJobSignals();
        s.jobId = jobId;
        s.jobEnrichmentId = jobEnrichmentId;

        // Try enrichment lookup first
        if (jobEnrichmentId != null) {
            enrichmentRepo.findById(jobEnrichmentId).ifPresent(enrichment -> {
                s.jobFamily = enrichment.getJobFamily();
                s.workMode = enrichment.getRemoteStatus();
                s.location = enrichment.getLocations();
                s.skills = parseSkills(enrichment.getSkills());

                rawJobRepo.findById(enrichment.getRawObservationId()).ifPresent(raw -> {
                    if (s.location == null) s.location = raw.getRawLocation();
                    if (raw.getJobIdentity() != null) {
                        s.companyName = raw.getJobIdentity().getCompanyName();
                    }
                });
            });
        }

        // If jobFamily or companyName still empty, check internal job repo
        if (jobId != null) {
            internalJobRepo.findById(jobId).ifPresent(j -> {
                if (s.workMode == null && j.getWorkMode() != null) s.workMode = j.getWorkMode().name();
                if (s.location == null) s.location = j.getLocation();
                if (s.companyName == null && j.getEmployer() != null) s.companyName = j.getEmployer().getCompanyName();
                if (s.skills == null || s.skills.isEmpty()) s.skills = j.getRequiredSkills();
                if (s.jobFamily == null && j.getTitle() != null) s.jobFamily = j.getTitle();
            });
        }

        // Fallback: if jobId was actually an enrichment ID
        if (s.jobFamily == null && s.companyName == null && jobId != null) {
            enrichmentRepo.findById(jobId).ifPresent(enrichment -> {
                s.jobEnrichmentId = enrichment.getId();
                s.jobFamily = enrichment.getJobFamily();
                s.workMode = enrichment.getRemoteStatus();
                s.location = enrichment.getLocations();
                s.skills = parseSkills(enrichment.getSkills());

                rawJobRepo.findById(enrichment.getRawObservationId()).ifPresent(raw -> {
                    if (s.location == null) s.location = raw.getRawLocation();
                    if (raw.getJobIdentity() != null) {
                        s.companyName = raw.getJobIdentity().getCompanyName();
                    }
                });
            });
        }

        return s;
    }

    private List<String> parseSkills(String skillsJsonOrCsv) {
        if (skillsJsonOrCsv == null || skillsJsonOrCsv.isBlank()) return Collections.emptyList();
        try {
            if (skillsJsonOrCsv.trim().startsWith("[")) {
                return objectMapper.readValue(skillsJsonOrCsv, new TypeReference<List<String>>() {});
            }
            return Arrays.stream(skillsJsonOrCsv.split(","))
                    .map(String::trim)
                    .filter(str -> !str.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Map<String, Double> readMap(String json) {
        if (json == null || json.isBlank()) return new HashMap<>();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Double>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private Set<Long> readSet(String json) {
        if (json == null || json.isBlank()) return new HashSet<>();
        try {
            return objectMapper.readValue(json, new TypeReference<Set<Long>>() {});
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    private String writeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }

    private static class ResolvedJobSignals {
        Long jobId;
        Long jobEnrichmentId;
        String jobFamily;
        String companyName;
        String workMode;
        String location;
        List<String> skills = new ArrayList<>();
    }
}
