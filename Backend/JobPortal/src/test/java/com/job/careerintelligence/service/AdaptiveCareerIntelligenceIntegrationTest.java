package com.job.careerintelligence.service;

import com.job.careerintelligence.dto.CandidateActionResponseDTO;
import com.job.careerintelligence.dto.CandidatePreferenceProfileDTO;
import com.job.careerintelligence.dto.RecommendationDTO;
import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.repository.*;
import com.job.entity.JobSeeker;
import com.job.enums.Role;
import com.job.repository.JobSeekerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class AdaptiveCareerIntelligenceIntegrationTest {

    @Autowired
    private AdaptiveRecommendationService adaptiveService;

    @Autowired
    private PersonalizedRecommendationService personalizedService;

    @Autowired
    private CandidateActionRepository actionRepo;

    @Autowired
    private CandidatePreferenceProfileRepository preferenceRepo;

    @Autowired
    private CandidateSemanticProfileRepository candidateProfileRepo;

    @Autowired
    private JobSemanticEnrichmentRepository enrichmentRepo;

    @Autowired
    private RawJobObservationRepository rawJobRepo;

    @Autowired
    private SourceJobIdentityRepository identityRepo;

    @Autowired
    private HybridMatchResultRepository hybridRepo;

    @Autowired
    private JobMatchResultRepository matchRepo;

    @Autowired
    private JobSeekerRepository seekerRepo;

    private JobSeeker testSeeker;
    private CandidateSemanticProfile testProfile;

    @BeforeEach
    public void setup() {
        testSeeker = new JobSeeker();
        testSeeker.setUsername("adaptive.seeker." + System.currentTimeMillis());
        testSeeker.setEmail("adaptive.seeker." + System.currentTimeMillis() + "@test.com");
        testSeeker.setPassword("password");
        testSeeker.setName("Adaptive Seeker");
        testSeeker.setRole(Role.JOB_SEEKER);
        testSeeker.setDob(LocalDate.of(1995, 5, 15));
        testSeeker = seekerRepo.save(testSeeker);

        testProfile = new CandidateSemanticProfile();
        testProfile.setJobSeekerId(testSeeker.getId());
        testProfile.setResumeObservationId(1L);
        testProfile.setIsLatestVersion(true);
        testProfile.setProcessingTimestamp(LocalDateTime.now());
        testProfile.setContentHash("hash" + System.currentTimeMillis());
        testProfile = candidateProfileRepo.save(testProfile);
    }

    private JobSemanticEnrichment createJob(String title, String company, String family, String workMode, String location, String skillsJson) {
        SourceJobIdentity identity = new SourceJobIdentity();
        identity.setSourcePlatform("TEST");
        identity.setCompanyName(company);
        identity.setExternalJobId("ext_" + UUID.randomUUID());
        identity = identityRepo.save(identity);

        RawJobObservation raw = new RawJobObservation();
        raw.setJobIdentity(identity);
        raw.setRawTitle(title);
        raw.setRawLocation(location);
        raw = rawJobRepo.save(raw);

        JobSemanticEnrichment enrichment = new JobSemanticEnrichment();
        enrichment.setRawObservationId(raw.getId());
        enrichment.setJobFamily(family);
        enrichment.setRemoteStatus(workMode);
        enrichment.setLocations(location);
        enrichment.setSkills(skillsJson);
        enrichment.setEmploymentType("FULL_TIME");
        return enrichmentRepo.save(enrichment);
    }

    @Test
    public void testViewActionUpdatesPreferenceProfile() {
        JobSemanticEnrichment job = createJob("ML Engineer", "Anthropic", "DATA_AI", "REMOTE", "San Francisco", "[\"Python\",\"PyTorch\"]");

        CandidateActionResponseDTO res = adaptiveService.recordAction(
                testSeeker.getId(), null, job.getId(), CandidateActionType.VIEW, "job_details_view"
        );

        assertNotNull(res.getActionId());
        assertEquals(CandidateActionType.VIEW, res.getActionType());
        assertEquals(1, res.getUpdatedInteractionCount());

        CandidatePreferenceProfileDTO profile = adaptiveService.getPreferenceProfileDTO(testSeeker.getId());
        assertEquals(1, profile.getInteractionCount());
        assertEquals(1.0, profile.getPreferredJobFamilies().get("DATA_AI"));
        assertEquals(1.0, profile.getPreferredWorkModes().get("REMOTE"));
        assertEquals(1.0, profile.getPreferredCompanies().get("Anthropic"));
        assertEquals(0.5, profile.getPreferredSkills().get("python"));
        assertEquals(0.5, profile.getPreferredSkills().get("pytorch"));
        assertTrue(profile.getSummary().contains("Learned from 1 interactions"));
    }

    @Test
    public void testSaveActionStrongerSignalThanView() {
        JobSemanticEnrichment job = createJob("Backend Dev", "Stripe", "ENGINEERING", "HYBRID", "Seattle", "[\"Java\",\"Spring\"]");

        CandidateActionResponseDTO res = adaptiveService.recordAction(
                testSeeker.getId(), null, job.getId(), CandidateActionType.SAVE, "saved_job"
        );

        assertEquals(3.0, CandidateActionType.SAVE.getWeight());
        CandidatePreferenceProfileDTO profile = adaptiveService.getPreferenceProfileDTO(testSeeker.getId());
        assertEquals(3.0, profile.getPreferredJobFamilies().get("ENGINEERING"));
        assertEquals(3.0, profile.getPreferredCompanies().get("Stripe"));
        assertEquals(1.5, profile.getPreferredSkills().get("java"));
    }

    @Test
    public void testApplyActionHighestSignal() {
        JobSemanticEnrichment job = createJob("Staff Architect", "Coinbase", "ENGINEERING", "REMOTE", "Remote", "[\"Go\",\"Distributed Systems\"]");

        adaptiveService.recordAction(testSeeker.getId(), null, job.getId(), CandidateActionType.APPLY, "direct_apply");

        assertEquals(5.0, CandidateActionType.APPLY.getWeight());
        CandidatePreferenceProfileDTO profile = adaptiveService.getPreferenceProfileDTO(testSeeker.getId());
        assertEquals(5.0, profile.getPreferredJobFamilies().get("ENGINEERING"));
        assertEquals(5.0, profile.getPreferredCompanies().get("Coinbase"));
        assertEquals(2.5, profile.getPreferredSkills().get("go"));
    }

    @Test
    public void testRejectActionDeterministicNegativeSignalWithoutDeletion() {
        JobSemanticEnrichment job = createJob("Sales Associate", "SalesCo", "SALES", "ON_SITE", "Austin", "[\"Cold Calling\"]");

        adaptiveService.recordAction(testSeeker.getId(), null, job.getId(), CandidateActionType.REJECT, "not_interested");

        CandidatePreferenceProfileDTO profile = adaptiveService.getPreferenceProfileDTO(testSeeker.getId());
        assertEquals(1, profile.getInteractionCount());
        assertEquals(-4.0, profile.getPreferredJobFamilies().get("SALES"));
        assertEquals(-4.0, profile.getPreferredCompanies().get("SalesCo"));
        assertEquals(-2.0, profile.getPreferredSkills().get("cold calling"));
        assertTrue(profile.getRejectedJobIds().contains(job.getId()));

        // Verify the job still exists in DB (not hard-filtered or deleted)
        assertTrue(enrichmentRepo.findById(job.getId()).isPresent());
    }

    @Test
    public void testAdaptiveReRankingAtRecommendationRankingLayer() {
        // Create Job A: Hybrid 80.0, Sales / On-Site
        JobSemanticEnrichment jobA = createJob("Sales Lead", "Salesforce", "SALES", "ON_SITE", "Chicago", "[\"Sales\"]");
        HybridMatchResult hybridA = new HybridMatchResult();
        hybridA.setCandidateProfileId(testProfile.getId());
        hybridA.setJobEnrichmentId(jobA.getId());
        hybridA.setDeterministicScore(80.0);
        hybridA.setSemanticScore(80.0);
        hybridA.setHybridScore(80.0);
        hybridA.setConfidence("HIGH");
        hybridA.setMatchedSkills("[\"Sales\"]");
        hybridRepo.save(hybridA);

        // Create Job B: Hybrid 78.0, AI / Remote (slightly lower hybrid score)
        JobSemanticEnrichment jobB = createJob("AI Researcher", "OpenAI", "DATA_AI", "REMOTE", "San Francisco", "[\"Python\",\"PyTorch\"]");
        HybridMatchResult hybridB = new HybridMatchResult();
        hybridB.setCandidateProfileId(testProfile.getId());
        hybridB.setJobEnrichmentId(jobB.getId());
        hybridB.setDeterministicScore(78.0);
        hybridB.setSemanticScore(78.0);
        hybridB.setHybridScore(78.0);
        hybridB.setConfidence("HIGH");
        hybridB.setMatchedSkills("[\"Python\",\"PyTorch\"]");
        hybridRepo.save(hybridB);

        // 1. Initial State (0 interactions): Job A (80.0) ranks higher than Job B (78.0)
        Page<RecommendationDTO> initialPage = personalizedService.getRecommendationsForSeeker(testSeeker.getId(), PageRequest.of(0, 10));
        assertEquals(2, initialPage.getTotalElements());
        assertEquals("Sales Lead", initialPage.getContent().get(0).getJobTitle());
        assertEquals("AI Researcher", initialPage.getContent().get(1).getJobTitle());
        assertEquals(80.0, initialPage.getContent().get(0).getHybridScore());

        // 2. Candidate interacts: APPLY to AI Researcher, REJECT Sales Lead
        adaptiveService.recordAction(testSeeker.getId(), null, jobB.getId(), CandidateActionType.APPLY, "apply_action");
        adaptiveService.recordAction(testSeeker.getId(), null, jobA.getId(), CandidateActionType.REJECT, "reject_action");

        // 3. Re-ranked state: Job B adapts and rises to the top!
        Page<RecommendationDTO> adaptedPage = personalizedService.getRecommendationsForSeeker(testSeeker.getId(), PageRequest.of(0, 10));
        assertEquals(2, adaptedPage.getTotalElements());

        RecommendationDTO topRec = adaptedPage.getContent().get(0);
        RecommendationDTO secondRec = adaptedPage.getContent().get(1);

        assertEquals("AI Researcher", topRec.getJobTitle());
        assertEquals("Sales Lead", secondRec.getJobTitle());

        // Confirm underlying Stage 4/5 scores remain 100% UNCHANGED
        assertEquals(78.0, topRec.getHybridScore());
        assertEquals(78.0, topRec.getDeterministicScore());
        assertEquals(80.0, secondRec.getHybridScore());
        assertEquals(80.0, secondRec.getDeterministicScore());

        // Confirm adaptive score re-ranked them
        assertTrue(topRec.getAdaptiveScore() > secondRec.getAdaptiveScore());
        assertTrue(topRec.getPreferenceScore() > secondRec.getPreferenceScore());
    }

    @Test
    public void testExplainablePreferenceSummary() {
        JobSemanticEnrichment job1 = createJob("ML Ops", "Nvidia", "DATA_AI", "REMOTE", "Santa Clara", "[\"Python\",\"CUDA\"]");
        JobSemanticEnrichment job2 = createJob("AI Eng", "Nvidia", "DATA_AI", "REMOTE", "Santa Clara", "[\"Python\",\"PyTorch\"]");

        adaptiveService.recordAction(testSeeker.getId(), null, job1.getId(), CandidateActionType.VIEW, "view");
        adaptiveService.recordAction(testSeeker.getId(), null, job2.getId(), CandidateActionType.APPLY, "apply");

        CandidatePreferenceProfileDTO profile = adaptiveService.getPreferenceProfileDTO(testSeeker.getId());
        assertNotNull(profile.getSummary());
        assertTrue(profile.getSummary().contains("Learned from 2 interactions"));
        assertTrue(profile.getSummary().contains("Top skills"));
        assertTrue(profile.getSummary().contains("python"));
        assertTrue(profile.getSummary().contains("Nvidia"));
    }
}
