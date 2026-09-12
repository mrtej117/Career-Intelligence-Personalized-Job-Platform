package com.job.controller;

import com.job.careerintelligence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class DebugController {
    
    private final CandidateSemanticProfileRepository candidateProfileRepo;
    private final JobMatchResultRepository jobMatchResultRepo;
    private final JobSemanticEnrichmentRepository jobEnrichmentRepo;
    private final RawJobObservationRepository rawJobRepo;
    private final com.job.repository.JobSeekerRepository jobSeekerRepo;
    private final com.job.repository.UserRepository userRepo;
    
    @GetMapping("/api/debug/counts")
    public Map<String, Long> getCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("JobSeekers", jobSeekerRepo.count());
        counts.put("CandidateProfiles", candidateProfileRepo.count());
        counts.put("JobMatchResults", jobMatchResultRepo.count());
        counts.put("JobEnrichments", jobEnrichmentRepo.count());
        counts.put("RawJobObservations", rawJobRepo.count());
        
        userRepo.findByUsername("seed.seeker.alice").ifPresent(alice -> {
            counts.put("AliceId", alice.getId());
            counts.put("AliceProfiles", (long) candidateProfileRepo.findTopByJobSeekerIdAndIsLatestVersionTrueOrderByProcessingTimestampDesc(alice.getId()).stream().count());
        });
        
        return counts;
    }
}
