package com.job.careerintelligence;

import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.repository.*;
import com.job.careerintelligence.service.DataCollectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class Phase2CollectionIntegrationTest {

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
    public void executePhase2Collection() {
        // 1. Setup Research and Registries for diverse companies
        setupCompany("Stripe", "Fintech", "Bengaluru", "Greenhouse");
        setupCompany("Gitlab", "DevOps/Tech", "Remote/India", "Greenhouse");
        setupCompany("Twilio", "Telecom", "Bengaluru", "Greenhouse");
        setupCompany("Airbnb", "Hospitality", "Bengaluru", "Greenhouse");
        setupCompany("Roblox", "Gaming", "Bengaluru", "Greenhouse");
        setupCompany("Coinbase", "Crypto/Finance", "Remote/India", "Greenhouse");

        // 2. Execute the generic collection cycle
        dataCollectionService.runCollectionCycle();

        // 3. Verify Collected Data
        List<SourceJobIdentity> identities = identityRepo.findAll();
        List<RawJobObservation> observations = observationRepo.findAll();
        List<UniversalJobRepresentation> universals = universalRepo.findAll();

        assertTrue(identities.size() > 0, "Should have collected source identities");
        assertTrue(observations.size() > 0, "Should have raw observations");
        assertEquals(identities.size(), observations.size(), "Each identity should have exactly 1 observation initially");
        
        System.out.println("TOTAL JOBS COLLECTED IN INDIA: " + observations.size());
        
        // 4. Test Deduplication
        // Run it again and expect zero new observations!
        dataCollectionService.runCollectionCycle();
        
        List<RawJobObservation> observationsAfterDup = observationRepo.findAll();
        assertEquals(observations.size(), observationsAfterDup.size(), "Deduplication failed: Duplicate observations were created!");

        // 5. Test Normalization Diversity
        boolean hasEngineering = universals.stream().anyMatch(u -> "ENGINEERING".equals(u.getJobFamily()));
        boolean hasOther = universals.stream().anyMatch(u -> !"ENGINEERING".equals(u.getJobFamily()));
        assertTrue(hasEngineering, "Should have some engineering roles");
        
        // 6. Print Summary for Report
        for (UniversalJobRepresentation u : universals) {
            System.out.println("Mapped Job: " + u.getUniversalTitle() + " | Level: " + u.getCareerLevel() + " | Family: " + u.getJobFamily());
            System.out.println("Extracted Qualifications: " + (u.getQualifications() != null ? "YES" : "NO"));
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
