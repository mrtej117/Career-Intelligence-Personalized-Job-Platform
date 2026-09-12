package com.job.careerintelligence;

import com.job.careerintelligence.adapter.GreenhouseAdapter;
import com.job.careerintelligence.dto.RawJobDTO;
import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.repository.*;
import com.job.careerintelligence.service.NormalizationService;
import com.job.careerintelligence.service.ObservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CareerIntelligenceIntegrationTest {

    @Autowired
    private GreenhouseAdapter greenhouseAdapter;

    @Autowired
    private ObservationService observationService;

    @Autowired
    private NormalizationService normalizationService;

    @Autowired
    private SourceJobIdentityRepository identityRepository;

    @Autowired
    private RawJobObservationRepository observationRepository;

    @Autowired
    private UniversalJobRepresentationRepository universalRepo;

    @Autowired
    private ResearchRecordRepository researchRepo;

    @Autowired
    private CareerSystemRegistryRepository registryRepo;

    @Test
    public void testFullPipelineAndLiveCollection() {
        // 1. Save Research Record
        ResearchRecord research = new ResearchRecord();
        research.setCompanyName("Stripe");
        research.setIndustry("Fintech");
        research.setCareerPlatform("Greenhouse");
        research.setResearchStatus("COMPLETED");
        researchRepo.save(research);
        assertNotNull(research.getId());

        // 2. Save Registry
        CareerSystemRegistry registry = new CareerSystemRegistry();
        registry.setCompanyName("Stripe");
        registry.setPlatformProvider("Greenhouse");
        registry.setObservablePublicRequests(true);
        registryRepo.save(registry);
        assertNotNull(registry.getId());

        // 3. Live Collection from Greenhouse (Stripe)
        // We use Stripe's greenhouse board as they are a large public company
        List<RawJobDTO> jobs = greenhouseAdapter.discoverJobs("stripe");
        assertFalse(jobs.isEmpty(), "Should fetch live jobs from Greenhouse");
        
        RawJobDTO firstJob = jobs.get(0);
        
        // Fetch details
        RawJobDTO detailedJob = greenhouseAdapter.fetchJobDetails("stripe", firstJob.getExternalJobId());
        assertNotNull(detailedJob.getRawHtml());

        // 4. Persistence & Deduplication
        RawJobObservation obs1 = observationService.saveObservation(detailedJob).getObservation();
        assertNotNull(obs1.getId());
        assertEquals("stripe", obs1.getJobIdentity().getCompanyName());

        // Fetch again, should not create a duplicate observation
        RawJobObservation obs2 = observationService.saveObservation(detailedJob).getObservation();
        assertEquals(obs1.getId(), obs2.getId(), "Should return the same observation due to matching hash");

        // Now simulate a change
        detailedJob.setRawJson("{\"changed\": true}");
        RawJobObservation obs3 = observationService.saveObservation(detailedJob).getObservation();
        assertNotEquals(obs1.getId(), obs3.getId(), "Should create a new observation because hash changed");
        assertEquals(obs1.getJobIdentity().getId(), obs3.getJobIdentity().getId(), "Should share the same SourceJobIdentity");

        // 5. Normalization
        UniversalJobRepresentation universal = normalizationService.normalize(obs3);
        assertNotNull(universal.getId());
        assertEquals(obs3.getId(), universal.getRawObservation().getId(), "Should be traceable to the raw observation");
        assertEquals(obs3.getRawTitle(), universal.getUniversalTitle());

        // 6. Edge Case Reporting
        EdgeCaseReport edgeCase = new EdgeCaseReport();
        edgeCase.setRawObservation(obs3);
        edgeCase.setIssueType("SCHEMA_GAP");
        edgeCase.setDescription("The raw HTML contains a custom section not present in our Universal Schema.");
        assertNotNull(edgeCase.getRawObservation());
        assertEquals("SCHEMA_GAP", edgeCase.getIssueType());
    }
}
