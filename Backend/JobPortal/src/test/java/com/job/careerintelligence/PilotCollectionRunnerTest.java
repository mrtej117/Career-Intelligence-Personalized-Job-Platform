package com.job.careerintelligence;

import com.job.careerintelligence.dto.CollectionMetrics;
import com.job.careerintelligence.entity.CareerSystemRegistry;
import com.job.careerintelligence.repository.CareerSystemRegistryRepository;
import com.job.careerintelligence.service.DataCollectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
        "career.intelligence.scheduler.enabled=false",
        "app.seed.enabled=false"
})
@ActiveProfiles("postgres")
public class PilotCollectionRunnerTest {

    @Autowired
    private CareerSystemRegistryRepository registryRepository;

    @Autowired
    private DataCollectionService dataCollectionService;

    @Test
    public void runPilotCollection() {
        // 1. Seed the 5 companies
        List<CareerSystemRegistry> pilots = Arrays.asList(
                createRegistry("GitLab", "Greenhouse", "gitlab"),
                createRegistry("Cursor", "Ashby", "cursor"),
                createRegistry("Cockroach Labs", "Greenhouse", "cockroachlabs"),
                createRegistry("Replit", "Ashby", "replit"),
                createRegistry("Palantir", "Lever", "palantir")
        );

        // Disable all existing to ensure ONLY the 5 pilots run
        for (CareerSystemRegistry r : registryRepository.findAll()) {
            r.setIsEnabled(false);
            registryRepository.save(r);
        }

        for (CareerSystemRegistry p : pilots) {
            Optional<CareerSystemRegistry> existing = registryRepository.findAll().stream()
                .filter(r -> r.getPlatformProvider() != null && r.getBoardToken() != null &&
                             r.getPlatformProvider().equals(p.getPlatformProvider()) && 
                             r.getBoardToken().equals(p.getBoardToken()))
                .findFirst();
            if (existing.isEmpty()) {
                registryRepository.save(p);
                System.out.println("Added: " + p.getCompanyName());
            } else {
                CareerSystemRegistry r = existing.get();
                r.setIsEnabled(true);
                registryRepository.save(r);
                System.out.println("Enabled existing: " + p.getCompanyName());
            }
        }

        System.out.println("\n--- STARTING PILOT COLLECTION CYCLE ---");
        CollectionMetrics metrics = dataCollectionService.runCollectionCycle();
        System.out.println("--- PILOT COLLECTION COMPLETED ---\n");

        System.out.println("Companies Attempted: " + metrics.getCompaniesAttempted());
        System.out.println("Companies Succeeded: " + metrics.getCompaniesSucceeded());
        System.out.println("Companies Failed: " + metrics.getCompaniesFailed());
        System.out.println("Jobs Discovered: " + metrics.getJobsDiscovered());
        System.out.println("Jobs Accepted (India/Remote): " + metrics.getJobsAccepted());
        System.out.println("NEW Jobs: " + metrics.getNewJobs());
        System.out.println("CHANGED Jobs: " + metrics.getChangedJobs());
        System.out.println("UNCHANGED Jobs: " + metrics.getUnchangedJobs());
        System.out.println("MISSING (Inactive) Jobs: " + metrics.getInactiveJobs());
        System.out.println("Duration: " + metrics.getDurationMs() + " ms");

        assertNotNull(metrics);
    }

    private CareerSystemRegistry createRegistry(String name, String platform, String token) {
        CareerSystemRegistry r = new CareerSystemRegistry();
        r.setCompanyName(name); 
        r.setPlatformProvider(platform);
        r.setBoardToken(token);
        r.setIsEnabled(true);
        return r;
    }
}
