package com.job.careerintelligence.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.dto.RecommendationDTO;
import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.repository.*;
import com.job.entity.JobSeeker;
import com.job.enums.Role;
import com.job.repository.JobSeekerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class PersonalizedRecommendationServiceIntegrationTest {

    @Autowired
    private PersonalizedRecommendationService service;

    @Autowired
    private JobMatchResultRepository matchRepo;
    @Autowired
    private CandidateSemanticProfileRepository candidateProfileRepo;
    @Autowired
    private JobSemanticEnrichmentRepository jobEnrichmentRepo;
    @Autowired
    private RawJobObservationRepository rawJobRepo;
    @Autowired
    private SourceJobIdentityRepository identityRepo;
    @Autowired
    private JobSeekerRepository seekerRepo;

    private final ObjectMapper om = new ObjectMapper();

    @Test
    public void testRecommendationFeed() throws Exception {
        // 1. Setup Seeker
        JobSeeker seeker = new JobSeeker();
        seeker.setUsername("test.seeker");
        seeker.setEmail("test.seeker@example.com");
        seeker.setPassword("password");
        seeker.setName("Test Seeker");
        seeker.setRole(Role.JOB_SEEKER);
        seeker.setDob(LocalDate.of(1990, 1, 1));
        seeker = seekerRepo.save(seeker);

        // 2. Setup Profile
        CandidateSemanticProfile profile = new CandidateSemanticProfile();
        profile.setJobSeekerId(seeker.getId());
        profile.setResumeObservationId(1L);
        profile.setIsLatestVersion(true);
        profile.setProcessingTimestamp(LocalDateTime.now());
        profile.setContentHash("hash123");
        profile = candidateProfileRepo.save(profile);

        // 3. Setup Jobs & Results
        for (int i = 1; i <= 3; i++) {
            SourceJobIdentity identity = new SourceJobIdentity();
            identity.setSourcePlatform("GREENHOUSE");
            identity.setCompanyName("Company " + i);
            identity.setExternalJobId("ext" + i);
            identity = identityRepo.save(identity);

            RawJobObservation raw = new RawJobObservation();
            raw.setJobIdentity(identity);
            raw.setRawTitle("Engineer " + i);
            raw.setRawLocation("Location " + i);
            raw = rawJobRepo.save(raw);

            JobSemanticEnrichment enrichment = new JobSemanticEnrichment();
            enrichment.setRawObservationId(raw.getId());
            enrichment.setEmploymentType("FULL_TIME");
            enrichment.setRemoteStatus("REMOTE");
            enrichment = jobEnrichmentRepo.save(enrichment);

            JobMatchResult match = new JobMatchResult();
            match.setCandidateProfileId(profile.getId());
            match.setJobEnrichmentId(enrichment.getId());
            match.setOverallScore(100.0 - (i * 10)); // 90, 80, 70
            match.setConfidence("HIGH");
            match.setExplanation("Explanation " + i);
            match.setMatchedSkillsJson("[\"Java\"]");
            match = matchRepo.save(match);
        }

        // 4. Test Service
        Page<RecommendationDTO> page = service.getRecommendationsForSeeker(seeker.getId(), PageRequest.of(0, 10));
        
        assertEquals(3, page.getTotalElements());
        assertEquals("Engineer 1", page.getContent().get(0).getJobTitle());
        assertEquals(90.0, page.getContent().get(0).getOverallScore());
        assertEquals("HIGH", page.getContent().get(0).getConfidence());
        
        // Determinism test
        Page<RecommendationDTO> page2 = service.getRecommendationsForSeeker(seeker.getId(), PageRequest.of(0, 10));
        assertEquals(page.getContent().get(0).getJobTitle(), page2.getContent().get(0).getJobTitle());
    }
}
