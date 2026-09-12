package com.job.careerintelligence;

import com.job.careerintelligence.dto.RawJobDTO;
import com.job.careerintelligence.dto.SaveObservationResult;
import com.job.careerintelligence.entity.CandidateSemanticProfile;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.entity.SourceJobIdentity;
import com.job.careerintelligence.matching.RealCandidateJobMatchingService;
import com.job.careerintelligence.repository.CandidateSemanticProfileRepository;
import com.job.careerintelligence.repository.JobSemanticEnrichmentRepository;
import com.job.careerintelligence.repository.RawJobObservationRepository;
import com.job.careerintelligence.repository.SourceJobIdentityRepository;
import com.job.careerintelligence.service.ObservationService;
import com.job.careerintelligence.service.SemanticEnrichmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("postgres")
public class ContinuousCollectionLifecycleIntegrationTest {

    @Autowired
    private ObservationService observationService;
    
    @Autowired
    private SemanticEnrichmentService enrichmentService;
    
    @Autowired
    private RealCandidateJobMatchingService matchingService;
    
    @Autowired
    private SourceJobIdentityRepository identityRepository;
    
    @Autowired
    private RawJobObservationRepository rawRepo;
    
    @Autowired
    private JobSemanticEnrichmentRepository enrichmentRepo;
    
    @Autowired
    private CandidateSemanticProfileRepository candidateRepo;

    @Test
    @Transactional
    public void testJobLifecycle() {
        String testCompany = "test-company-" + System.currentTimeMillis();
        String testExternalId = "test-job-999";
        
        // ----------------------------------------------------
        // PHASE 1: NEW JOB
        // ----------------------------------------------------
        RawJobDTO dto = new RawJobDTO();
        dto.setSourcePlatform("TEST_PLATFORM");
        dto.setCompanyName(testCompany);
        dto.setExternalJobId(testExternalId);
        dto.setRawTitle("Test Software Engineer");
        dto.setRawLocation("Remote India");
        dto.setRawJson("{\"title\": \"Software Engineer v1\"}"); // Hash base
        
        SaveObservationResult newResult = observationService.saveObservation(dto);
        assertEquals(SaveObservationResult.ObservationStatus.NEW, newResult.getStatus());
        assertNotNull(newResult.getObservation());
        assertEquals(1, identityRepository.findBySourcePlatformAndCompanyNameAndIsActiveTrue("TEST_PLATFORM", testCompany).size());
        
        SourceJobIdentity identity = newResult.getObservation().getJobIdentity();
        assertEquals(0, identity.getMissingCyclesCount());
        
        // Ensure Llama picks it up
        List<JobSemanticEnrichment> newlyEnriched = enrichmentService.processBatch(10);
        // We might pick up other un-enriched jobs in the DB if any exist, but it should contain our fixture
        boolean foundNew = newlyEnriched.stream().anyMatch(e -> e.getRawObservationId().equals(newResult.getObservation().getId()));
        assertTrue(foundNew, "New job must be enriched");
        
        // Ensure Matching is triggered
        RealCandidateJobMatchingService.BatchMatchResult matchResult = matchingService.matchSpecificJobsToAllCandidates(newlyEnriched);
        assertNotNull(matchResult);
        
        // ----------------------------------------------------
        // PHASE 2: UNCHANGED JOB
        // ----------------------------------------------------
        SaveObservationResult unchangedResult = observationService.saveObservation(dto);
        assertEquals(SaveObservationResult.ObservationStatus.UNCHANGED, unchangedResult.getStatus());
        assertEquals(newResult.getObservation().getId(), unchangedResult.getObservation().getId(), "Should return the same RawJobObservation ID");
        
        // Ensure Llama is skipped
        List<JobSemanticEnrichment> unchangedEnriched = enrichmentService.processBatch(10);
        boolean foundUnchanged = unchangedEnriched.stream().anyMatch(e -> e.getRawObservationId().equals(unchangedResult.getObservation().getId()));
        assertFalse(foundUnchanged, "Unchanged job must NOT be enriched again");
        
        // ----------------------------------------------------
        // PHASE 3: CHANGED JOB
        // ----------------------------------------------------
        dto.setRawJson("{\"title\": \"Software Engineer v2\"}"); // Alter content
        SaveObservationResult changedResult = observationService.saveObservation(dto);
        assertEquals(SaveObservationResult.ObservationStatus.CHANGED, changedResult.getStatus());
        assertNotEquals(newResult.getObservation().getId(), changedResult.getObservation().getId(), "Must create NEW observation ID");
        assertEquals(identity.getId(), changedResult.getObservation().getJobIdentity().getId(), "Must share same Identity");
        
        // Verify historical is preserved
        long totalObservationsForIdentity = rawRepo.findAll().stream()
            .filter(r -> r.getJobIdentity().getId().equals(identity.getId()))
            .count();
        assertEquals(2, totalObservationsForIdentity, "Both old and new observations must be preserved");
        
        // Ensure Llama picks up the change
        List<JobSemanticEnrichment> changedEnriched = enrichmentService.processBatch(10);
        boolean foundChanged = changedEnriched.stream().anyMatch(e -> e.getRawObservationId().equals(changedResult.getObservation().getId()));
        assertTrue(foundChanged, "Changed job must be enriched");
        
        // ----------------------------------------------------
        // PHASE 4: MISSING JOB
        // ----------------------------------------------------
        // Simulate missing from collection (threshold 3)
        List<String> emptyActiveIds = Collections.emptyList();
        
        // Cycle 1 missing
        int newlyInactiveCount = observationService.processMissingJobs("TEST_PLATFORM", testCompany, emptyActiveIds, 3);
        assertEquals(0, newlyInactiveCount);
        assertEquals(1, identityRepository.findById(identity.getId()).get().getMissingCyclesCount());
        
        // Cycle 2 missing
        newlyInactiveCount = observationService.processMissingJobs("TEST_PLATFORM", testCompany, emptyActiveIds, 3);
        assertEquals(0, newlyInactiveCount);
        assertEquals(2, identityRepository.findById(identity.getId()).get().getMissingCyclesCount());
        
        // Cycle 3 missing -> Hits threshold
        newlyInactiveCount = observationService.processMissingJobs("TEST_PLATFORM", testCompany, emptyActiveIds, 3);
        assertEquals(1, newlyInactiveCount); // Should now be marked inactive
        
        SourceJobIdentity finalIdentity = identityRepository.findById(identity.getId()).get();
        assertEquals(3, finalIdentity.getMissingCyclesCount());
        assertFalse(finalIdentity.getIsActive(), "Job should be inactive after reaching threshold");
        
        // Verify historicals still exist!
        long finalObservations = rawRepo.findAll().stream()
            .filter(r -> r.getJobIdentity().getId().equals(identity.getId()))
            .count();
        assertEquals(2, finalObservations, "RawObservations must NOT be deleted when job goes inactive");
    }
}
