package com.job.careerintelligence.matching;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.entity.CandidateSemanticProfile;
import com.job.careerintelligence.entity.JobMatchResult;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.repository.JobMatchResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Orchestrates the explainable candidate ↔ job matching.
 * Purely deterministic — does NOT call LLM for matching.
 *
 * <h3>Default weight configuration (v1.0):</h3>
 * <ul>
 *   <li>Skills: 40%</li>
 *   <li>Experience: 20%</li>
 *   <li>Career Level: 15%</li>
 *   <li>Job Family: 10%</li>
 *   <li>Education: 5%</li>
 *   <li>Location: 5%</li>
 *   <li>Work Mode: 5%</li>
 * </ul>
 * <p>These are initial baseline weights that require validation against
 * manually reviewed candidate/job pairs.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobMatchingService {

    private final SkillMatcher skillMatcher;
    private final ExperienceMatcher experienceMatcher;
    private final CareerLevelMatcher careerLevelMatcher;
    private final JobFamilyMatcher jobFamilyMatcher;
    private final EducationMatcher educationMatcher;
    private final LocationMatcher locationMatcher;
    private final WorkModeMatcher workModeMatcher;
    private final JobMatchResultRepository matchResultRepo;
    private final ObjectMapper objectMapper;

    // Configurable weights — initial baseline v1.0
    public static final String SCORING_VERSION = "v1.0";
    private static final double W_SKILL = 0.40;
    private static final double W_EXPERIENCE = 0.20;
    private static final double W_CAREER_LEVEL = 0.15;
    private static final double W_JOB_FAMILY = 0.10;
    private static final double W_EDUCATION = 0.05;
    private static final double W_LOCATION = 0.05;
    private static final double W_WORK_MODE = 0.05;

    @Transactional
    public JobMatchResult calculateMatch(CandidateSemanticProfile candidate, JobSemanticEnrichment job) {
        // 1. Parse candidate skills from JSON
        List<String> candidateSkills = parseJsonArray(candidate.getSkillsJson());
        List<String> jobRequiredSkills = parseJsonArray(job.getSkills());
        List<String> jobPreferredSkills = parseJsonArray(job.getPreferredSkills());

        // 2. Run individual matchers
        SkillMatcher.SkillMatchResult skillResult = skillMatcher.match(candidateSkills, jobRequiredSkills, jobPreferredSkills);
        DimensionResult expResult = experienceMatcher.match(candidate.getTotalExperienceYears(), job.getExperienceYearsMin(), job.getExperienceYearsMax());
        DimensionResult levelResult = careerLevelMatcher.match(candidate.getCareerLevel(), job.getCareerLevel());
        DimensionResult familyResult = jobFamilyMatcher.match(inferCandidateJobFamily(candidate), job.getJobFamily());
        DimensionResult eduResult = educationMatcher.match(candidate.getEducationJson(), job.getEducation());
        DimensionResult locResult = locationMatcher.match(candidate.getLocationPreferencesJson(), job.getLocations());
        DimensionResult workResult = workModeMatcher.match(candidate.getLocationPreferencesJson(), job.getRemoteStatus());

        // 3. Calculate weighted overall score
        double overall = (skillResult.getScore() * W_SKILL)
                + (expResult.getScore() * W_EXPERIENCE)
                + (levelResult.getScore() * W_CAREER_LEVEL)
                + (familyResult.getScore() * W_JOB_FAMILY)
                + (eduResult.getScore() * W_EDUCATION)
                + (locResult.getScore() * W_LOCATION)
                + (workResult.getScore() * W_WORK_MODE);

        overall = Math.max(0, Math.min(100, overall));

        // 4. Aggregate strengths and gaps
        List<String> allStrengths = new ArrayList<>();
        allStrengths.addAll(skillResult.getStrengths());
        allStrengths.addAll(expResult.getStrengths());
        allStrengths.addAll(levelResult.getStrengths());
        allStrengths.addAll(familyResult.getStrengths());
        allStrengths.addAll(eduResult.getStrengths());
        allStrengths.addAll(locResult.getStrengths());
        allStrengths.addAll(workResult.getStrengths());

        List<String> allGaps = new ArrayList<>();
        allGaps.addAll(skillResult.getGaps());
        allGaps.addAll(expResult.getGaps());
        allGaps.addAll(levelResult.getGaps());
        allGaps.addAll(familyResult.getGaps());
        allGaps.addAll(eduResult.getGaps());
        allGaps.addAll(locResult.getGaps());
        allGaps.addAll(workResult.getGaps());

        // 5. Calculate confidence
        String confidence = calculateConfidence(candidate, job);

        // 6. Build explanation
        String explanation = buildExplanation(overall, confidence, allStrengths, allGaps);

        // 7. Build and persist result
        JobMatchResult result = matchResultRepo.findByCandidateProfileIdAndJobEnrichmentId(candidate.getId(), job.getId())
                .orElse(new JobMatchResult());

        result.setCandidateProfileId(candidate.getId());
        result.setJobEnrichmentId(job.getId());
        result.setOverallScore(Math.round(overall * 100.0) / 100.0);
        result.setSkillScore(Math.round(skillResult.getScore() * 100.0) / 100.0);
        result.setExperienceScore(Math.round(expResult.getScore() * 100.0) / 100.0);
        result.setCareerLevelScore(Math.round(levelResult.getScore() * 100.0) / 100.0);
        result.setJobFamilyScore(Math.round(familyResult.getScore() * 100.0) / 100.0);
        result.setEducationScore(Math.round(eduResult.getScore() * 100.0) / 100.0);
        result.setLocationScore(Math.round(locResult.getScore() * 100.0) / 100.0);
        result.setWorkModeScore(Math.round(workResult.getScore() * 100.0) / 100.0);
        result.setMatchedSkillsJson(toJson(skillResult.getMatchedRequired()));
        result.setMissingSkillsJson(toJson(skillResult.getMissingRequired()));
        result.setPreferredSkillsMatchedJson(toJson(skillResult.getMatchedPreferred()));
        result.setStrengthsJson(toJson(allStrengths));
        result.setGapsJson(toJson(allGaps));
        result.setExplanation(explanation);
        result.setConfidence(confidence);
        result.setScoringVersion(SCORING_VERSION);
        result.setCalculatedAt(LocalDateTime.now());

        return matchResultRepo.save(result);
    }

    private String calculateConfidence(CandidateSemanticProfile candidate, JobSemanticEnrichment job) {
        int availableFields = 0;
        int totalFields = 7;

        if (candidate.getSkillsJson() != null && !"[]".equals(candidate.getSkillsJson())) availableFields++;
        if (candidate.getTotalExperienceYears() != null) availableFields++;
        if (candidate.getCareerLevel() != null) availableFields++;
        if (candidate.getEducationJson() != null && !"[]".equals(candidate.getEducationJson())) availableFields++;
        if (job.getSkills() != null && !job.getSkills().isEmpty()) availableFields++;
        if (job.getCareerLevel() != null) availableFields++;
        if (job.getExperienceYearsMin() != null) availableFields++;

        double ratio = (double) availableFields / totalFields;
        if (ratio >= 0.85) return "HIGH";
        if (ratio >= 0.55) return "MEDIUM";
        return "LOW";
    }

    private String buildExplanation(double score, String confidence, List<String> strengths, List<String> gaps) {
        StringBuilder sb = new StringBuilder();
        sb.append("MATCH SCORE: ").append(String.format("%.0f", score)).append("/100\n");
        sb.append("CONFIDENCE: ").append(confidence).append("\n\n");

        if (!strengths.isEmpty()) {
            sb.append("STRENGTHS:\n");
            for (String s : strengths) {
                sb.append("  + ").append(s).append("\n");
            }
        }

        if (!gaps.isEmpty()) {
            sb.append("\nGAPS:\n");
            for (String g : gaps) {
                sb.append("  - ").append(g).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Infer candidate job family from their professional title (uses same logic as NormalizationService).
     */
    private String inferCandidateJobFamily(CandidateSemanticProfile candidate) {
        String title = candidate.getProfessionalTitle();
        if (title == null) return null;
        String t = title.toLowerCase();

        if (t.contains("engineer") || t.contains("developer") || t.contains("architect") || t.contains("programmer")) return "ENGINEERING";
        if (t.contains("data") || t.contains("ai") || t.contains("machine learning") || t.contains("ml") || t.contains("analytics")) return "DATA_AI";
        if (t.contains("product")) return "PRODUCT";
        if (t.contains("design") || t.contains("ux") || t.contains("ui")) return "DESIGN_UX";
        if (t.contains("market") || t.contains("brand") || t.contains("growth")) return "MARKETING";
        if (t.contains("hr") || t.contains("talent") || t.contains("recruit") || t.contains("people")) return "HR";
        if (t.contains("finance") || t.contains("account") || t.contains("tax") || t.contains("audit")) return "FINANCE";
        if (t.contains("sales") || t.contains("business development")) return "SALES";
        if (t.contains("operation") || t.contains("logistics")) return "OPERATIONS";
        if (t.contains("consult")) return "CONSULTING";
        if (t.contains("research") || t.contains("scientist")) return "RESEARCH";
        return null;
    }

    private List<String> parseJsonArray(String json) {
        if (json == null || json.trim().isEmpty()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
