package com.job.careerintelligence;

import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.repository.*;
import com.job.careerintelligence.service.DataCollectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MultiSourceIntegrationTest {

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private CareerSystemRegistryRepository registryRepo;

    @Autowired
    private ResearchRecordRepository researchRepo;

    @Autowired
    private RawJobObservationRepository observationRepo;

    @Autowired
    private UniversalJobRepresentationRepository universalRepo;

    @Autowired
    private SourceJobIdentityRepository identityRepo;

    @Test
    public void executeMultiSourceCollection() {
        // Setup companies to run the test
        setupCompany("Stripe", "Fintech", "Bengaluru", "Greenhouse");
        setupCompany("paytm", "Fintech", "Remote/India", "Lever");

        // 1. Run first collection cycle
        dataCollectionService.runCollectionCycle();

        List<SourceJobIdentity> identities = identityRepo.findAll();
        List<RawJobObservation> observations = observationRepo.findAll();
        List<UniversalJobRepresentation> universals = universalRepo.findAll();

        assertTrue(identities.size() > 0, "Should have collected source identities");
        assertTrue(observations.size() > 0, "Should have raw observations");
        assertEquals(identities.size(), observations.size(), "Each identity should have exactly 1 observation initially");
        
        boolean hasGreenhouse = identities.stream().anyMatch(i -> "Greenhouse".equals(i.getSourcePlatform()));
        boolean hasLever = identities.stream().anyMatch(i -> "Lever".equals(i.getSourcePlatform()));
        
        assertTrue(hasGreenhouse, "Should have Greenhouse jobs");
        assertTrue(hasLever, "Should have Lever jobs");

        System.out.println("TOTAL JOBS COLLECTED: " + observations.size());
        
        // Ensure no collisions - check unique company + externalId constraint implicitly verified by DB saves
        
        // 2. Test Deduplication / Change Tracking across both sources
        dataCollectionService.runCollectionCycle();
        
        List<RawJobObservation> observationsAfterDup = observationRepo.findAll();
        assertEquals(observations.size(), observationsAfterDup.size(), "Deduplication failed: Duplicate observations were created!");

        // 3. Verify cross-source normalization
        for (UniversalJobRepresentation u : universals) {
            assertNotNull(u.getUniversalTitle());
            assertNotNull(u.getNormalizedLocation());
            // They both go through the exact same NormalizationService
        }
    }

    private void setupCompany(String name, String industry, String city, String platform) {
        ResearchRecord research = new ResearchRecord();
        research.setCompanyName(name);
        research.setIndustry(industry);
        research.setIndianCity(city);
        research.setCareerPlatform(platform);
        research.setResearchStatus("COMPLETED");
        researchRepo.save(research);

        CareerSystemRegistry registry = new CareerSystemRegistry();
        registry.setCompanyName(name);
        registry.setPlatformProvider(platform);
        registry.setObservablePublicRequests(true);
        registryRepo.save(registry);
    }
}
