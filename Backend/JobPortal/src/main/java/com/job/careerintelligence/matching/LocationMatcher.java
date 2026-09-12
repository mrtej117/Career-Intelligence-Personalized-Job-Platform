package com.job.careerintelligence.matching;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Location matcher: compares candidate locations against job locations.
 * Remote jobs match any candidate location.
 */
@Slf4j
@Component
public class LocationMatcher {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DimensionResult match(String candidateLocationJson, String jobLocationsStr) {
        List<String> strengths = new ArrayList<>();
        List<String> gaps = new ArrayList<>();

        List<String> candidateLocations = parseLocations(candidateLocationJson);
        List<String> jobLocations = parseSimpleJsonArray(jobLocationsStr);

        if (jobLocations.isEmpty()) {
            strengths.add("No specific location requirement");
            return new DimensionResult("location", 100.0, strengths, gaps);
        }

        if (candidateLocations.isEmpty()) {
            gaps.add("Candidate location is unknown");
            return new DimensionResult("location", 50.0, strengths, gaps);
        }

        // Normalize all to lowercase
        Set<String> candNorm = new HashSet<>();
        candidateLocations.forEach(l -> candNorm.add(l.toLowerCase().trim()));

        Set<String> jobNorm = new HashSet<>();
        jobLocations.forEach(l -> jobNorm.add(l.toLowerCase().trim()));

        // Check for India-wide match
        boolean jobInIndia = jobNorm.stream().anyMatch(l -> l.contains("india"));
        boolean candInIndia = candNorm.stream().anyMatch(l -> l.contains("india"));

        // Check for exact city overlap
        boolean cityMatch = false;
        for (String cLoc : candNorm) {
            for (String jLoc : jobNorm) {
                if (cLoc.contains(jLoc) || jLoc.contains(cLoc)) {
                    cityMatch = true;
                    break;
                }
            }
            if (cityMatch) break;
        }

        double score;
        if (cityMatch) {
            score = 100.0;
            strengths.add("Location match: candidate and job share a location");
        } else if (jobInIndia && candInIndia) {
            score = 75.0;
            strengths.add("Both candidate and job are India-based");
        } else {
            score = 25.0;
            gaps.add("Location mismatch: candidate in " + candidateLocations + " vs job in " + jobLocations);
        }

        return new DimensionResult("location", score, strengths, gaps);
    }

    private List<String> parseLocations(String json) {
        if (json == null || json.trim().isEmpty()) return Collections.emptyList();
        try {
            // Try parsing as { "locations": [...] } structure
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {});
            Object locs = map.get("locations");
            if (locs instanceof List) {
                List<String> result = new ArrayList<>();
                for (Object o : (List<?>) locs) {
                    if (o != null) result.add(o.toString());
                }
                return result;
            }
        } catch (Exception e) {
            // Fall through to simple array parsing
        }
        return parseSimpleJsonArray(json);
    }

    private List<String> parseSimpleJsonArray(String json) {
        if (json == null || json.trim().isEmpty()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            // Not a JSON array; treat as single value
            return Collections.singletonList(json);
        }
    }
}
