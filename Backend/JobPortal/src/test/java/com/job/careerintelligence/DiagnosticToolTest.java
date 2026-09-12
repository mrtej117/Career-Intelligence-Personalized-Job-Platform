package com.job.careerintelligence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("postgres")
public class DiagnosticToolTest {

    @Autowired private RawJobObservationRepository rawJobRepo;
    @Autowired private JobSemanticEnrichmentRepository jobEnrichmentRepo;
    @Autowired private CandidateSemanticProfileRepository candidateProfileRepo;
    @Autowired private SemanticEmbeddingRepository embeddingRepo;
    @Autowired private JobMatchResultRepository matchRepo;
    @Autowired private HybridMatchResultRepository hybridRepo;
    @Autowired private SemanticSimilarityResultRepository semanticResultRepo;

    @Test
    @Transactional(readOnly = true)
    public void runDiagnostics() {
        System.out.println("==================================================");
        System.out.println("DIAGNOSTIC REPORT START");
        System.out.println("==================================================");

        List<RawJobObservation> allRawJobs = rawJobRepo.findAll();
        System.out.println("1. Number of active Career Intelligence jobs: " + allRawJobs.size());

        Set<String> companies = allRawJobs.stream()
                .filter(j -> j.getJobIdentity() != null)
                .map(j -> j.getJobIdentity().getCompanyName())
                .collect(Collectors.toSet());
        System.out.println("2. Number of distinct companies: " + companies.size());

        Set<String> locations = allRawJobs.stream()
                .map(RawJobObservation::getRawLocation)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        System.out.println("3. Number of distinct locations: " + locations.size());

        System.out.println("4. Jobs per company:");
        allRawJobs.stream()
                .filter(j -> j.getJobIdentity() != null)
                .collect(Collectors.groupingBy(j -> j.getJobIdentity().getCompanyName(), Collectors.counting()))
                .forEach((c, count) -> System.out.println("   " + c + ": " + count));

        System.out.println("5. Jobs per location:");
        allRawJobs.stream()
                .filter(j -> j.getRawLocation() != null)
                .collect(Collectors.groupingBy(RawJobObservation::getRawLocation, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(15)
                .forEach(e -> System.out.println("   " + e.getKey() + ": " + e.getValue()));

        List<CandidateSemanticProfile> candidates = candidateProfileRepo.findAll();
        if (candidates.isEmpty()) {
            System.out.println("No candidates found!");
            return;
        }

        CandidateSemanticProfile candidate = candidates.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsLatestVersion()))
                .max(Comparator.comparing(CandidateSemanticProfile::getProcessingTimestamp))
                .orElse(candidates.get(0));

        System.out.println("6. Candidate ID: " + candidate.getId());
        System.out.println("   Location Pref: " + candidate.getLocationPreferencesJson());
        System.out.println("   Skills: " + candidate.getSkillsJson());

        List<SemanticEmbedding> allEmbeddings = embeddingRepo.findAll();
        long candEmbCount = allEmbeddings.stream().filter(e -> "CANDIDATE".equals(e.getEntityType()) && e.getEntityId().equals(candidate.getId())).count();
        long jobEmbCount = allEmbeddings.stream().filter(e -> "JOB".equals(e.getEntityType())).count();
        System.out.println("7. Embeddings exist? Candidate: " + candEmbCount + ", Total Jobs with Embeddings: " + jobEmbCount);

        long semResCount = semanticResultRepo.count();
        System.out.println("8. Semantic Similarity Results in DB: " + semResCount);

        System.out.println("9. Top 20 Recommendations by Deterministic Score:");
        Page<JobMatchResult> top20 = matchRepo.findByCandidateProfileIdOrderByOverallScoreDesc(candidate.getId(), PageRequest.of(0, 20));
        
        for (JobMatchResult m : top20.getContent()) {
            double sem = 0.0;
            double hyb = 0.0;
            Optional<HybridMatchResult> hybOpt = hybridRepo.findByCandidateProfileIdAndJobEnrichmentId(candidate.getId(), m.getJobEnrichmentId());
            if (hybOpt.isPresent()) {
                sem = hybOpt.get().getSemanticScore();
                hyb = hybOpt.get().getHybridScore();
            }

            String company = "Unknown";
            String location = "Unknown";
            Optional<JobSemanticEnrichment> enr = jobEnrichmentRepo.findById(m.getJobEnrichmentId());
            if (enr.isPresent()) {
                Optional<RawJobObservation> raw = rawJobRepo.findById(enr.get().getRawObservationId());
                if (raw.isPresent()) {
                    if (raw.get().getJobIdentity() != null) {
                        company = raw.get().getJobIdentity().getCompanyName();
                    }
                    location = raw.get().getRawLocation();
                }
            }

            System.out.printf("   - Company: %s, Location: %s, Det: %.2f, Sem: %.2f, Hyb: %.2f\n", 
                company, location, m.getOverallScore(), sem, hyb);
        }
        System.out.println("==================================================");
    }
}
