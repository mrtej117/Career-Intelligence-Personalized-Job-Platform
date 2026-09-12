package com.job.careerintelligence.matching;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Work mode matcher: compares candidate remote preference against job remote status.
 */
@Slf4j
@Component
public class WorkModeMatcher {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DimensionResult match(String candidateLocationPrefJson, String jobRemoteStatus) {
        List<String> strengths = new ArrayList<>();
        List<String> gaps = new ArrayList<>();

        String candidatePref = extractRemotePreference(candidateLocationPrefJson);
        String jobMode = jobRemoteStatus != null ? jobRemoteStatus.toUpperCase().trim() : "UNKNOWN";

        if ("UNKNOWN".equals(jobMode)) {
            strengths.add("Job work mode is unspecified — no penalty");
            return new DimensionResult("workMode", 100.0, strengths, gaps);
        }

        if ("UNKNOWN".equals(candidatePref)) {
            gaps.add("Candidate work mode preference is unknown");
            return new DimensionResult("workMode", 75.0, strengths, gaps);
        }

        double score;
        if (candidatePref.equals(jobMode)) {
            score = 100.0;
            strengths.add("Work mode match: both " + jobMode);
        } else if ("REMOTE".equals(jobMode)) {
            // Remote jobs are compatible with everyone
            score = 90.0;
            strengths.add("Job is REMOTE — compatible with any candidate preference");
        } else if ("HYBRID".equals(jobMode) && ("REMOTE".equals(candidatePref) || "ONSITE".equals(candidatePref))) {
            score = 70.0;
            strengths.add("Job is HYBRID — partially compatible with candidate preference " + candidatePref);
        } else {
            score = 40.0;
            gaps.add("Work mode mismatch: candidate prefers " + candidatePref + " but job is " + jobMode);
        }

        return new DimensionResult("workMode", score, strengths, gaps);
    }

    private String extractRemotePreference(String json) {
        if (json == null || json.trim().isEmpty()) return "UNKNOWN";
        try {
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {});
            Object pref = map.get("remotePreference");
            if (pref != null) return pref.toString().toUpperCase().trim();
        } catch (Exception e) {
            log.debug("Could not parse location preferences JSON: {}", e.getMessage());
        }
        return "UNKNOWN";
    }
}
