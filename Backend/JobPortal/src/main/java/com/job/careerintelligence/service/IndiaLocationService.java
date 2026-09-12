package com.job.careerintelligence.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Service that encapsulates all India/Remote‑India location detection logic.
 * Keeps the DataCollectionService free of hard‑coded strings and makes the
 * matching criteria maintainable and testable.
 */
@Service
public class IndiaLocationService {

    private static final Set<String> INDIA_KEYWORDS = new HashSet<>(Arrays.asList(
            // generic country mention
            "india",
            // remote variants
            "remote - india",
            "remote india",
            "remote‑india",
            // major cities & common variants
            "bengaluru",
            "bangalore",
            "hyderabad",
            "chennai",
            "mumbai",
            "pune",
            "delhi",
            "new delhi",
            "gurugram",
            "gurgaon",
            "noida",
            "kolkata",
            "ahmedabad",
            "jaipur",
            "chandigarh",
            "kochi",
            "cochin",
            "thiruvananthapuram",
            "indore",
            "bhubaneswar",
            "visakhapatnam",
            "vijayawada"
    ));

    /**
     * Determines whether the supplied raw location string should be considered an
     * Indian location (including remote‑India postings).
     *
     * @param rawLocation the location string from the job board (may be null)
     * @return true if the location matches any of the supported India patterns
     */
    public boolean isValidLocation(String rawLocation) {
        if (rawLocation == null) {
            return false;
        }
        String lower = rawLocation.toLowerCase();
        // Fast path for explicit country name
        if (lower.contains("india")) {
            return true;
        }
        // Check against the keyword set – includes cities, states and remote phrases
        return INDIA_KEYWORDS.stream().anyMatch(lower::contains);
    }
}
