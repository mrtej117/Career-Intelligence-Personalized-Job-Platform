package com.job.careerintelligence;

import com.job.careerintelligence.dto.RecommendationDTO;
import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.repository.*;
import com.job.careerintelligence.service.PersonalizedRecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "career.intelligence.scheduler.enabled=false",
        "app.seed.enabled=false"
})
@ActiveProfiles("postgres")
@Transactional
public class Stage78VerificationTest {

    @Autowired
    private SourceJobIdentityRepository identityRepo;

    @Autowired
    private RawJobObservationRepository rawRepo;

    @Autowired
    private JobSemanticEnrichmentRepository enrichmentRepo;

    @Autowired
    private SemanticEmbeddingRepository embeddingRepo;

    @Autowired
    private JobMatchResultRepository matchRepo;

    @Autowired
    private SemanticSimilarityResultRepository similarityRepo;

    @Autowired
    private HybridMatchResultRepository hybridRepo;

    @Autowired
    private CandidateSemanticProfileRepository candidateRepo;

    @Autowired
    private PersonalizedRecommendationService recommendationService;

    @Test
    public void runStage78PostgresVerification() {
        System.out.println("\n==========================================");
        System.out.println("STAGE 7.8 POSTGRES REAL-RUNTIME AUDIT");
        System.out.println("==========================================");

        // 1. Exact NEW and CHANGED IDs
        List<Long> targetNewIdentityIds = List.of(360L, 361L, 362L, 363L, 364L, 365L, 366L, 367L, 368L);
        Long targetChangedIdentityId = 358L;

        System.out.println("1. Identifying Affected Jobs in PostgreSQL:");
        for (Long id : targetNewIdentityIds) {
            SourceJobIdentity identity = identityRepo.findById(id).orElseThrow();
            List<RawJobObservation> raws = rawRepo.findAll().stream()
                    .filter(r -> r.getJobIdentity() != null && r.getJobIdentity().getId().equals(id))
                    .toList();
            assertEquals(1, raws.size(), "NEW job must have exactly 1 raw observation");
            RawJobObservation latestRaw = raws.get(0);
            JobSemanticEnrichment enr = enrichmentRepo.findByRawObservationId(latestRaw.getId());
            assertNotNull(enr, "NEW job must have enrichment");
            assertEquals("COMPLETED", enr.getProcessingStatus());
            System.out.println("   [NEW] Job Identity ID: " + id + " | Company: " + identity.getCompanyName() + " | Raw ID: " + latestRaw.getId() + " | Enrichment ID: " + enr.getId());
        }

        // CHANGED Job (Identity 358)
        SourceJobIdentity changedIdentity = identityRepo.findById(targetChangedIdentityId).orElseThrow();
        List<RawJobObservation> changedRaws = rawRepo.findAll().stream()
                .filter(r -> r.getJobIdentity() != null && r.getJobIdentity().getId().equals(targetChangedIdentityId))
                .sorted(Comparator.comparing(RawJobObservation::getCollectionTimestamp))
                .toList();
        assertTrue(changedRaws.size() >= 2, "CHANGED job must preserve historical observations (>= 2)");
        RawJobObservation oldObs = changedRaws.get(0);
        RawJobObservation newObs = changedRaws.get(changedRaws.size() - 1);
        System.out.println("   [CHANGED] Job Identity ID: " + targetChangedIdentityId + " | Company: " + changedIdentity.getCompanyName());
        System.out.println("             Historical Raw ID: " + oldObs.getId() + " at " + oldObs.getCollectionTimestamp());
        System.out.println("             Latest Raw ID: " + newObs.getId() + " at " + newObs.getCollectionTimestamp());

        JobSemanticEnrichment oldEnr = enrichmentRepo.findByRawObservationId(oldObs.getId());
        JobSemanticEnrichment newEnr = enrichmentRepo.findByRawObservationId(newObs.getId());
        assertNotNull(oldEnr, "Historical observation enrichment must be preserved");
        assertNotNull(newEnr, "New observation enrichment must be created");
        System.out.println("             Historical Enrichment ID: " + oldEnr.getId() + " (status: " + oldEnr.getProcessingStatus() + ")");
        System.out.println("             New Enrichment ID: " + newEnr.getId() + " (status: " + newEnr.getProcessingStatus() + ")");

        // 2. UNCHANGED count confirmation
        List<SourceJobIdentity> pilotIdentities = identityRepo.findAll().stream()
                .filter(i -> List.of("gitlab", "cursor", "cockroachlabs", "replit", "palantir").contains(i.getCompanyName().toLowerCase()))
                .filter(SourceJobIdentity::getIsActive)
                .toList();
        int totalPilotActive = pilotIdentities.size();
        System.out.println("\n2. Active Pilot Identities in PostgreSQL: " + totalPilotActive);

        // Count unchanged and missing jobs
        long unchangedOrMissingCount = pilotIdentities.stream()
                .filter(i -> !targetNewIdentityIds.contains(i.getId()) && !targetChangedIdentityId.equals(i.getId()))
                .count();
        System.out.println("   Confirmed Existing (39 UNCHANGED + 6 MISSING Cycle 1) Count: " + unchangedOrMissingCount);
        assertTrue(unchangedOrMissingCount >= 39, "Should have at least 39 UNCHANGED jobs");

        // 3. Duplicate checks
        System.out.println("\n3. Deduplication Audit in PostgreSQL:");
        
        // Check duplicate raw observations for same identity & same hash
        List<RawJobObservation> allRaws = rawRepo.findAll();
        Map<String, Long> rawHashCounts = allRaws.stream()
                .filter(r -> r.getJobIdentity() != null)
                .collect(Collectors.groupingBy(r -> r.getJobIdentity().getId() + "_" + r.getContentHash(), Collectors.counting()));
        long duplicateRaws = rawHashCounts.values().stream().filter(c -> c > 1).count();
        System.out.println("   Duplicate Raw Observations: " + duplicateRaws);
        assertEquals(0, duplicateRaws, "No duplicate RawJobObservations should exist with identical hash");

        // Check duplicate enrichments per raw observation
        List<JobSemanticEnrichment> allEnrichments = enrichmentRepo.findAll();
        Map<Long, Long> enrCounts = allEnrichments.stream()
                .collect(Collectors.groupingBy(JobSemanticEnrichment::getRawObservationId, Collectors.counting()));
        long duplicateEnrichments = enrCounts.values().stream().filter(c -> c > 1).count();
        System.out.println("   Duplicate Enrichments per Raw Observation: " + duplicateEnrichments);
        assertEquals(0, duplicateEnrichments, "No duplicate enrichments per raw observation");

        // Check duplicate matches per candidate & job enrichment
        List<JobMatchResult> allMatches = matchRepo.findAll();
        Map<String, Long> matchCounts = allMatches.stream()
                .collect(Collectors.groupingBy(m -> m.getCandidateProfileId() + "_" + m.getJobEnrichmentId(), Collectors.counting()));
        long duplicateMatches = matchCounts.values().stream().filter(c -> c > 1).count();
        System.out.println("   Duplicate Deterministic Matches: " + duplicateMatches);
        assertEquals(0, duplicateMatches, "No duplicate deterministic matches");

        // Check duplicate semantic similarity per candidate & job enrichment
        List<SemanticSimilarityResult> allSims = similarityRepo.findAll();
        Map<String, Long> simCounts = allSims.stream()
                .collect(Collectors.groupingBy(s -> s.getCandidateProfileId() + "_" + s.getJobEnrichmentId(), Collectors.counting()));
        long duplicateSims = simCounts.values().stream().filter(c -> c > 1).count();
        System.out.println("   Duplicate Semantic Similarity Records: " + duplicateSims);
        assertEquals(0, duplicateSims, "No duplicate semantic similarities");

        // Check duplicate hybrid records
        List<HybridMatchResult> allHybrids = hybridRepo.findAll();
        Map<String, Long> hybridCounts = allHybrids.stream()
                .collect(Collectors.groupingBy(h -> h.getCandidateProfileId() + "_" + h.getJobEnrichmentId(), Collectors.counting()));
        long duplicateHybrids = hybridCounts.values().stream().filter(c -> c > 1).count();
        System.out.println("   Duplicate Hybrid Records: " + duplicateHybrids);
        assertEquals(0, duplicateHybrids, "No duplicate hybrid records");

        // 4. Recommendation Retrieval Endpoint Verification
        System.out.println("\n4. Recommendation Retrieval Verification:");
        List<CandidateSemanticProfile> profiles = candidateRepo.findAll().stream()
                .filter(CandidateSemanticProfile::getIsLatestVersion)
                .toList();
        System.out.println("   Active Candidate Profiles: " + profiles.size());

        boolean foundNewJobInRecs = false;
        Set<Long> affectedEnrichmentIds = new HashSet<>();
        for (Long id : targetNewIdentityIds) {
            rawRepo.findFirstByJobIdentityIdOrderByCollectionTimestampDesc(id)
                    .ifPresent(r -> {
                        JobSemanticEnrichment e = enrichmentRepo.findByRawObservationId(r.getId());
                        if (e != null) affectedEnrichmentIds.add(e.getId());
                    });
        }
        rawRepo.findFirstByJobIdentityIdOrderByCollectionTimestampDesc(targetChangedIdentityId)
                .ifPresent(r -> {
                    JobSemanticEnrichment e = enrichmentRepo.findByRawObservationId(r.getId());
                    if (e != null) affectedEnrichmentIds.add(e.getId());
                });

        System.out.println("   Target Affected Enrichment IDs: " + affectedEnrichmentIds);

        for (CandidateSemanticProfile profile : profiles) {
            Page<RecommendationDTO> page = recommendationService.getRecommendationsForSeeker(profile.getJobSeekerId(), PageRequest.of(0, 50));
            assertNotNull(page);
            for (RecommendationDTO rec : page.getContent()) {
                if (affectedEnrichmentIds.contains(rec.getJobEnrichmentId())) {
                    foundNewJobInRecs = true;
                    System.out.println("   SUCCESS: Candidate " + profile.getJobSeekerId() + " received recommendation for newly processed JobEnrichmentId=" 
                            + rec.getJobEnrichmentId() + " (" + rec.getJobTitle() + " at " + rec.getCompanyName() 
                            + ") with Hybrid Score: " + rec.getHybridScore());
                    break;
                }
            }
            if (foundNewJobInRecs) break;
        }

        assertTrue(foundNewJobInRecs, "At least one newly enriched job must appear in recommendations");

        System.out.println("\n==========================================");
        System.out.println("STAGE 7.8 ALL REAL-RUNTIME CHECKS PASSED");
        System.out.println("==========================================\n");
    }
}
