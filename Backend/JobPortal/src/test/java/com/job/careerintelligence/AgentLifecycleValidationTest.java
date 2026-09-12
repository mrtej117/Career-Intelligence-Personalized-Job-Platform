package com.job.careerintelligence;

import com.job.careerintelligence.agent.AgentObservationSnapshot;
import com.job.careerintelligence.agent.ObservationSnapshotService;
import com.job.careerintelligence.entity.RawJobObservation;
import com.job.careerintelligence.entity.SourceJobIdentity;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.llm.LocalLlmClient;
import com.job.careerintelligence.repository.JobSemanticEnrichmentRepository;
import com.job.careerintelligence.repository.RawJobObservationRepository;
import com.job.careerintelligence.repository.SemanticEmbeddingRepository;
import com.job.careerintelligence.repository.SourceJobIdentityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "career.intelligence.scheduler.enabled=false",
        "app.seed.enabled=false"
})
@ActiveProfiles("dev") // Uses H2
public class AgentLifecycleValidationTest {

    @Autowired
    private ObservationSnapshotService snapshotService;

    @Autowired
    private SourceJobIdentityRepository identityRepo;

    @Autowired
    private RawJobObservationRepository rawRepo;

    @Autowired
    private JobSemanticEnrichmentRepository enrichmentRepo;

    @MockBean
    private SemanticEmbeddingRepository embeddingRepo;

    @MockBean
    private LocalLlmClient llmClient;

    @BeforeEach
    public void setup() {
        identityRepo.deleteAll();
        rawRepo.deleteAll();
        enrichmentRepo.deleteAll();

        when(llmClient.isModelAvailable()).thenReturn(true);
        when(llmClient.getModelName()).thenReturn("test-model");
        
        when(embeddingRepo.findByEntityTypeAndEntityIdAndModelName(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(java.util.Optional.empty());
    }

    @Test
    public void testAgentLifecycleBehaviors() {
        // --- CASE C & D: UNCHANGED JOB and Historical Observation Preserved ---
        SourceJobIdentity unchangedIdentity = new SourceJobIdentity();
        unchangedIdentity.setSourcePlatform("Greenhouse");
        unchangedIdentity.setCompanyName("GitLab");
        unchangedIdentity.setExternalJobId("unchanged-123");
        unchangedIdentity.setIsActive(true);
        unchangedIdentity = identityRepo.save(unchangedIdentity);

        // Historical observation (Day 1)
        RawJobObservation oldUnchangedObs = new RawJobObservation();
        oldUnchangedObs.setJobIdentity(unchangedIdentity);
        oldUnchangedObs.setCollectionTimestamp(LocalDateTime.now().minusDays(1));
        oldUnchangedObs = rawRepo.save(oldUnchangedObs);

        // Since it's unchanged, there is NO new RawJobObservation (Collection logic handles this, returning existing hash).
        // It should ALREADY have an enrichment from Day 1.
        JobSemanticEnrichment oldEnrichment = new JobSemanticEnrichment();
        oldEnrichment.setRawObservationId(oldUnchangedObs.getId());
        oldEnrichment.setProcessingStatus("COMPLETED");
        oldEnrichment = enrichmentRepo.save(oldEnrichment);
        
        when(embeddingRepo.findByEntityTypeAndEntityIdAndModelName(
                org.mockito.ArgumentMatchers.eq("JOB_ENRICHMENT"),
                org.mockito.ArgumentMatchers.eq(oldEnrichment.getId()),
                org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(java.util.Optional.of(new com.job.careerintelligence.entity.SemanticEmbedding()));


        // --- CASE A: NEW JOB ---
        SourceJobIdentity newIdentity = new SourceJobIdentity();
        newIdentity.setSourcePlatform("Lever");
        newIdentity.setCompanyName("Palantir");
        newIdentity.setExternalJobId("new-456");
        newIdentity.setIsActive(true);
        newIdentity = identityRepo.save(newIdentity);

        RawJobObservation newObs = new RawJobObservation();
        newObs.setJobIdentity(newIdentity);
        newObs.setCollectionTimestamp(LocalDateTime.now());
        newObs = rawRepo.save(newObs);
        // NO enrichment yet


        // --- CASE B & D: CHANGED JOB and Historical Observation Preserved ---
        SourceJobIdentity changedIdentity = new SourceJobIdentity();
        changedIdentity.setSourcePlatform("Ashby");
        changedIdentity.setCompanyName("Cursor");
        changedIdentity.setExternalJobId("changed-789");
        changedIdentity.setIsActive(true);
        changedIdentity = identityRepo.save(changedIdentity);

        // Historical observation (Day 1)
        RawJobObservation oldChangedObs = new RawJobObservation();
        oldChangedObs.setJobIdentity(changedIdentity);
        oldChangedObs.setCollectionTimestamp(LocalDateTime.now().minusDays(1));
        oldChangedObs = rawRepo.save(oldChangedObs);
        
        JobSemanticEnrichment oldChangedEnrichment = new JobSemanticEnrichment();
        oldChangedEnrichment.setRawObservationId(oldChangedObs.getId());
        oldChangedEnrichment.setProcessingStatus("COMPLETED");
        enrichmentRepo.save(oldChangedEnrichment);

        // NEW observation for the CHANGED job (Day 2)
        RawJobObservation newChangedObs = new RawJobObservation();
        newChangedObs.setJobIdentity(changedIdentity);
        newChangedObs.setCollectionTimestamp(LocalDateTime.now());
        newChangedObs = rawRepo.save(newChangedObs);
        // NO enrichment for the new observation yet


        // Act: Take Snapshot
        AgentObservationSnapshot snapshot = snapshotService.takeSnapshot();

        // Assert
        assertEquals(3, snapshot.getActiveJobsCount());
        
        // UNCHANGED should NOT be in jobsToEnrich (proving Case C)
        assertFalse(snapshot.getJobsToEnrich().contains(oldUnchangedObs.getId()));
        
        // NEW should be in jobsToEnrich (proving Case A)
        assertTrue(snapshot.getJobsToEnrich().contains(newObs.getId()));
        
        // CHANGED should be in jobsToEnrich with the NEW observation ID (proving Case B & D)
        assertTrue(snapshot.getJobsToEnrich().contains(newChangedObs.getId()));
        assertFalse(snapshot.getJobsToEnrich().contains(oldChangedObs.getId()));
    }
}
