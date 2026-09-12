package com.job.careerintelligence;

import com.job.careerintelligence.agent.AgentObservationSnapshot;
import com.job.careerintelligence.agent.CareerIntelligenceAgent;
import com.job.careerintelligence.agent.ObservationSnapshotService;
import com.job.careerintelligence.entity.RawJobObservation;
import com.job.careerintelligence.repository.JobSemanticEnrichmentRepository;
import com.job.careerintelligence.repository.RawJobObservationRepository;
import com.job.careerintelligence.repository.HybridMatchResultRepository;
import com.job.careerintelligence.repository.SemanticEmbeddingRepository;
import com.job.careerintelligence.repository.SourceJobIdentityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
        "career.intelligence.scheduler.enabled=false",
        "app.seed.enabled=false"
})
@ActiveProfiles("postgres")
public class Stage78AgentRunnerTest {

    @Autowired
    private CareerIntelligenceAgent agent;

    @Autowired
    private ObservationSnapshotService snapshotService;

    @Autowired
    private RawJobObservationRepository rawRepo;

    @Autowired
    private SourceJobIdentityRepository identityRepo;

    @Autowired
    private JobSemanticEnrichmentRepository enrichmentRepo;

    @Autowired
    private SemanticEmbeddingRepository embeddingRepo;

    @Autowired
    private HybridMatchResultRepository hybridRepo;

    @Test
    public void runPilotAgentValidation() {
        System.out.println("\n=== STAGE 7.8: PILOT AI ENRICHMENT VALIDATION ===");

        long initialRecommendations = hybridRepo.count();
        long initialEmbeddings = embeddingRepo.count();

        AgentObservationSnapshot snapshot = snapshotService.takeSnapshot();
        System.out.println("Observation Snapshot taken.");
        System.out.println("Active Jobs: " + snapshot.getActiveJobsCount());
        
        List<Long> jobsToEnrich = snapshot.getJobsToEnrich();
        System.out.println("Jobs targeted for Llama enrichment: " + jobsToEnrich.size());

        List<Long> newJobIds = new ArrayList<>();
        List<Long> changedJobIds = new ArrayList<>();

        for (Long rawId : jobsToEnrich) {
            RawJobObservation obs = rawRepo.findById(rawId).orElseThrow();
            long count = rawRepo.countByJobIdentityId(obs.getJobIdentity().getId());
            if (count > 1) {
                changedJobIds.add(obs.getJobIdentity().getId());
                System.out.println("Targeting CHANGED job: Identity ID=" + obs.getJobIdentity().getId() + " (Raw ID=" + rawId + ")");
            } else {
                newJobIds.add(obs.getJobIdentity().getId());
                System.out.println("Targeting NEW job: Identity ID=" + obs.getJobIdentity().getId() + " (Raw ID=" + rawId + ")");
            }
        }

        System.out.println("\n--- RUNNING AGENT CYCLE ---");
        agent.runExecution();
        System.out.println("--- AGENT CYCLE COMPLETED ---\n");

        long finalRecommendations = hybridRepo.count();
        long finalEmbeddings = embeddingRepo.count();

        System.out.println("Embeddings generated: " + (finalEmbeddings - initialEmbeddings));
        System.out.println("Recommendations generated: " + (finalRecommendations - initialRecommendations));

        System.out.println("EXACT NEW JOB IDs PROCESSED: " + newJobIds);
        System.out.println("EXACT CHANGED JOB IDs PROCESSED: " + changedJobIds);
        
        assertNotNull(snapshot);
    }
}
