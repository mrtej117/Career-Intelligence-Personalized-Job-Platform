package com.job.careerintelligence.matching;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Job family compatibility matrix.
 * Same families score 100; related families score moderately; unrelated score low.
 */
@Component
public class JobFamilyMatcher {

    // Related family groupings — families within the same group are considered related
    private static final Map<String, Set<String>> RELATED_FAMILIES = new HashMap<>();

    static {
        Set<String> techGroup = new HashSet<>(Arrays.asList("ENGINEERING", "DATA_AI", "RESEARCH"));
        Set<String> businessGroup = new HashSet<>(Arrays.asList("PRODUCT", "CONSULTING", "OPERATIONS"));
        Set<String> commercialGroup = new HashSet<>(Arrays.asList("SALES", "MARKETING"));
        Set<String> financeGroup = new HashSet<>(Arrays.asList("FINANCE"));
        Set<String> peopleGroup = new HashSet<>(Arrays.asList("HR", "RECRUITMENT"));
        Set<String> creativeGroup = new HashSet<>(Arrays.asList("DESIGN_UX", "MARKETING"));

        for (String family : techGroup) RELATED_FAMILIES.put(family, techGroup);
        for (String family : businessGroup) RELATED_FAMILIES.put(family, businessGroup);
        for (String family : commercialGroup) RELATED_FAMILIES.put(family, commercialGroup);
        for (String family : financeGroup) RELATED_FAMILIES.put(family, financeGroup);
        for (String family : peopleGroup) RELATED_FAMILIES.put(family, peopleGroup);
        // For creative group, merge with any existing entry (e.g., MARKETING already mapped to commercialGroup)
        for (String family : creativeGroup) {
            Set<String> existing = RELATED_FAMILIES.get(family);
            if (existing != null) {
                existing.addAll(creativeGroup);
            } else {
                RELATED_FAMILIES.put(family, new HashSet<>(creativeGroup));
            }
        }
    }

    public DimensionResult match(String candidateFamily, String jobFamily) {
        List<String> strengths = new ArrayList<>();
        List<String> gaps = new ArrayList<>();

        if (candidateFamily == null || jobFamily == null) {
            gaps.add("Job family unknown for " + (candidateFamily == null ? "candidate" : "job"));
            return new DimensionResult("jobFamily", 50.0, strengths, gaps);
        }

        String cNorm = candidateFamily.toUpperCase();
        String jNorm = jobFamily.toUpperCase();

        if ("OTHER".equals(cNorm) || "OTHER".equals(jNorm)) {
            gaps.add("Job family is OTHER — insufficient classification data");
            return new DimensionResult("jobFamily", 40.0, strengths, gaps);
        }

        if (cNorm.equals(jNorm)) {
            strengths.add("Job family match: " + cNorm);
            return new DimensionResult("jobFamily", 100.0, strengths, gaps);
        }

        Set<String> relatedToCandidate = RELATED_FAMILIES.getOrDefault(cNorm, Collections.emptySet());
        if (relatedToCandidate.contains(jNorm)) {
            strengths.add("Related job families: " + cNorm + " ↔ " + jNorm);
            return new DimensionResult("jobFamily", 65.0, strengths, gaps);
        }

        gaps.add("Job family mismatch: candidate=" + cNorm + ", job=" + jNorm);
        return new DimensionResult("jobFamily", 20.0, strengths, gaps);
    }
}
