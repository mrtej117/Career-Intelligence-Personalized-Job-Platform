package com.job;

import com.job.careerintelligence.repository.JobMatchResultRepository;
import com.job.careerintelligence.repository.JobSemanticEnrichmentRepository;
import com.job.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
public class StatTest {
    @Autowired JobMatchResultRepository r1;
    @Autowired JobSemanticEnrichmentRepository r2;
    @Autowired JobRepository r3;

    @Test
    public void printStats() {
        System.out.println("========= STATS =========");
        System.out.println("JobMatchResult count: " + r1.count());
        System.out.println("JobSemanticEnrichment count: " + r2.count());
        System.out.println("Job count: " + r3.count());
        
        r1.findAll().stream().limit(1).forEach(jmr -> {
            System.out.println("Sample Match Result: " + jmr);
        });
        System.out.println("=========================");
    }
}
