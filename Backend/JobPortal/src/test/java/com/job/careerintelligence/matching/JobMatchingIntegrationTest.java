package com.job.careerintelligence.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.entity.CandidateSemanticProfile;
import com.job.careerintelligence.entity.JobMatchResult;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.repository.JobMatchResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class JobMatchingIntegrationTest {

    @Autowired
    private JobMatchingService matchingService;

    @Autowired
    private JobMatchResultRepository matchResultRepo;

    private final ObjectMapper om = new ObjectMapper();

    // ─── Helper builders ───────────────────────────────────────────────

    private CandidateSemanticProfile buildCandidate(String title, String level, Integer exp,
                                                     List<String> skills, String educationJson,
                                                     String locationJson) throws Exception {
        CandidateSemanticProfile c = new CandidateSemanticProfile();
        c.setId(1L);
        c.setJobSeekerId(1L);
        c.setResumeObservationId(1L);
        c.setContentHash("test-hash");
        c.setProcessingStatus("COMPLETED");
        c.setProcessingTimestamp(LocalDateTime.now());
        c.setModelName("llama3.2");
        c.setIsLatestVersion(true);
        c.setProfessionalTitle(title);
        c.setCareerLevel(level);
        c.setTotalExperienceYears(exp);
        c.setSkillsJson(om.writeValueAsString(skills));
        c.setEducationJson(educationJson);
        c.setLocationPreferencesJson(locationJson);
        return c;
    }

    private JobSemanticEnrichment buildJob(String family, String level, Integer minExp, Integer maxExp,
                                           List<String> required, List<String> preferred,
                                           String education, String remote, String locations) throws Exception {
        JobSemanticEnrichment j = new JobSemanticEnrichment();
        j.setId(1L);
        j.setRawObservationId(1L);
        j.setProcessingStatus("COMPLETED");
        j.setProcessingTimestamp(LocalDateTime.now());
        j.setModelName("llama3.2");
        j.setJobFamily(family);
        j.setCareerLevel(level);
        j.setExperienceYearsMin(minExp);
        j.setExperienceYearsMax(maxExp);
        j.setSkills(om.writeValueAsString(required));
        j.setPreferredSkills(om.writeValueAsString(preferred));
        j.setEducation(education);
        j.setRemoteStatus(remote);
        j.setLocations(locations);
        return j;
    }

    // ─── Test 1: Strong engineering match ───────────────────────────────
    @Test
    public void testStrongEngineeringMatch() throws Exception {
        CandidateSemanticProfile candidate = buildCandidate(
                "Senior Backend Engineer", "SENIOR", 8,
                Arrays.asList("Java", "Spring Boot", "PostgreSQL", "Docker", "Kubernetes", "AWS"),
                "[{\"degree\":\"B.Tech\",\"fieldOfStudy\":\"Computer Science\",\"institution\":\"NIT\"}]",
                "{\"remotePreference\":\"REMOTE\",\"locations\":[\"Bengaluru\"]}"
        );

        JobSemanticEnrichment job = buildJob(
                "ENGINEERING", "SENIOR", 5, 10,
                Arrays.asList("Java", "Spring Boot", "PostgreSQL"),
                Arrays.asList("Docker", "AWS"),
                "Bachelor's degree in Computer Science",
                "REMOTE",
                "[\"Bengaluru\", \"India\"]"
        );

        JobMatchResult result = matchingService.calculateMatch(candidate, job);

        System.out.println("=== STRONG ENGINEERING MATCH ===");
        System.out.println(result.getExplanation());
        System.out.println("Overall: " + result.getOverallScore());

        assertNotNull(result);
        assertTrue(result.getOverallScore() >= 80, "Strong match should score >= 80, got " + result.getOverallScore());
        assertEquals(100.0, result.getSkillScore(), "All required skills matched");
        assertEquals("HIGH", result.getConfidence());
        assertTrue(result.getExplanation().contains("STRENGTHS"));
    }

    // ─── Test 2: Weak engineering match (missing skills) ────────────────
    @Test
    public void testWeakEngineeringMatch() throws Exception {
        CandidateSemanticProfile candidate = buildCandidate(
                "Junior Developer", "ENTRY_LEVEL", 1,
                Arrays.asList("HTML", "CSS", "JavaScript"),
                null,
                "{\"remotePreference\":\"ONSITE\",\"locations\":[\"Mumbai\"]}"
        );

        JobSemanticEnrichment job = buildJob(
                "ENGINEERING", "SENIOR", 5, null,
                Arrays.asList("Java", "Spring Boot", "PostgreSQL", "Kafka", "Docker"),
                Arrays.asList("Kubernetes", "AWS"),
                "Master's degree in Computer Science",
                "ONSITE",
                "[\"Bengaluru\"]"
        );

        JobMatchResult result = matchingService.calculateMatch(candidate, job);

        System.out.println("\n=== WEAK ENGINEERING MATCH ===");
        System.out.println(result.getExplanation());
        System.out.println("Overall: " + result.getOverallScore());

        assertNotNull(result);
        assertTrue(result.getOverallScore() < 40, "Weak match should score < 40, got " + result.getOverallScore());
        assertTrue(result.getExplanation().contains("GAPS"));
    }

    // ─── Test 3: Experience exceeds requirement ─────────────────────────
    @Test
    public void testExperienceExceeds() throws Exception {
        CandidateSemanticProfile candidate = buildCandidate(
                "Staff Engineer", "LEAD", 15,
                Arrays.asList("Java", "Spring Boot"),
                null,
                "{\"remotePreference\":\"UNKNOWN\",\"locations\":[]}"
        );

        JobSemanticEnrichment job = buildJob(
                "ENGINEERING", "SENIOR", 3, 8,
                Arrays.asList("Java", "Spring Boot"),
                Collections.emptyList(), null, "UNKNOWN", "[]"
        );

        JobMatchResult result = matchingService.calculateMatch(candidate, job);

        System.out.println("\n=== EXPERIENCE EXCEEDS ===");
        System.out.println(result.getExplanation());

        assertTrue(result.getExperienceScore() >= 80, "Overqualified should still score well");
    }

    // ─── Test 4: Unknown experience on both sides ───────────────────────
    @Test
    public void testUnknownExperience() throws Exception {
        CandidateSemanticProfile candidate = buildCandidate(
                "Analyst", "EXPERIENCED", null,
                Arrays.asList("Excel", "SQL"),
                null,
                "{\"remotePreference\":\"UNKNOWN\",\"locations\":[]}"
        );

        JobSemanticEnrichment job = buildJob(
                "FINANCE", "EXPERIENCED", null, null,
                Arrays.asList("Excel", "SQL"),
                Collections.emptyList(), null, "UNKNOWN", "[]"
        );

        JobMatchResult result = matchingService.calculateMatch(candidate, job);

        System.out.println("\n=== UNKNOWN EXPERIENCE ===");
        System.out.println(result.getExplanation());

        assertTrue(result.getExperienceScore() >= 50, "Unknown experience should not crash or heavily penalize");
    }

    // ─── Test 5: Career level mismatch ──────────────────────────────────
    @Test
    public void testCareerLevelMismatch() throws Exception {
        CandidateSemanticProfile candidate = buildCandidate(
                "Intern", "INTERNSHIP", 0,
                Arrays.asList("Python"),
                null,
                "{\"remotePreference\":\"UNKNOWN\",\"locations\":[]}"
        );

        JobSemanticEnrichment job = buildJob(
                "ENGINEERING", "DIRECTOR", 15, null,
                Arrays.asList("Python", "Leadership"),
                Collections.emptyList(), null, "ONSITE", "[\"Delhi\"]"
        );

        JobMatchResult result = matchingService.calculateMatch(candidate, job);

        System.out.println("\n=== CAREER LEVEL MISMATCH ===");
        System.out.println(result.getExplanation());

        assertTrue(result.getCareerLevelScore() < 30, "Intern vs Director should score very low");
    }

    // ─── Test 6: Job family mismatch ────────────────────────────────────
    @Test
    public void testJobFamilyMismatch() throws Exception {
        CandidateSemanticProfile candidate = buildCandidate(
                "Marketing Manager", "MANAGER", 10,
                Arrays.asList("Google Ads", "SEO", "Content Marketing"),
                null,
                "{\"remotePreference\":\"REMOTE\",\"locations\":[\"Delhi\"]}"
        );

        JobSemanticEnrichment job = buildJob(
                "ENGINEERING", "MANAGER", 8, null,
                Arrays.asList("Java", "Microservices"),
                Collections.emptyList(), null, "REMOTE", "[\"India\"]"
        );

        JobMatchResult result = matchingService.calculateMatch(candidate, job);

        System.out.println("\n=== JOB FAMILY MISMATCH ===");
        System.out.println(result.getExplanation());

        assertTrue(result.getJobFamilyScore() <= 30, "Marketing vs Engineering should score low");
    }

    // ─── Test 7: Related job families ───────────────────────────────────
    @Test
    public void testRelatedJobFamilies() throws Exception {
        CandidateSemanticProfile candidate = buildCandidate(
                "Data Scientist", "SENIOR", 6,
                Arrays.asList("Python", "TensorFlow", "SQL"),
                null,
                "{\"remotePreference\":\"REMOTE\",\"locations\":[\"Bengaluru\"]}"
        );

        JobSemanticEnrichment job = buildJob(
                "ENGINEERING", "SENIOR", 5, null,
                Arrays.asList("Python", "SQL"),
                Arrays.asList("TensorFlow"),
                null, "REMOTE", "[\"India\"]"
        );

        JobMatchResult result = matchingService.calculateMatch(candidate, job);

        System.out.println("\n=== RELATED JOB FAMILIES (DATA_AI vs ENGINEERING) ===");
        System.out.println(result.getExplanation());

        assertTrue(result.getJobFamilyScore() >= 60, "DATA_AI and ENGINEERING should be related");
    }

    // ─── Test 8: Remote compatibility ───────────────────────────────────
    @Test
    public void testRemoteCompatibility() throws Exception {
        CandidateSemanticProfile candidate = buildCandidate(
                "Software Engineer", "EXPERIENCED", 4,
                Arrays.asList("Java"),
                null,
                "{\"remotePreference\":\"REMOTE\",\"locations\":[\"Chennai\"]}"
        );

        JobSemanticEnrichment job = buildJob(
                "ENGINEERING", "EXPERIENCED", 3, null,
                Arrays.asList("Java"),
                Collections.emptyList(), null, "ONSITE", "[\"Mumbai\"]"
        );

        JobMatchResult result = matchingService.calculateMatch(candidate, job);

        System.out.println("\n=== REMOTE vs ONSITE ===");
        System.out.println(result.getExplanation());

        assertTrue(result.getWorkModeScore() < 50, "REMOTE candidate vs ONSITE job should penalize");
    }

    // ─── Test 9: Deterministic reproducibility ──────────────────────────
    @Test
    public void testDeterministicReproducibility() throws Exception {
        CandidateSemanticProfile candidate = buildCandidate(
                "Backend Engineer", "SENIOR", 7,
                Arrays.asList("Java", "Spring Boot", "Docker"),
                "[{\"degree\":\"B.Tech\",\"fieldOfStudy\":\"Computer Science\"}]",
                "{\"remotePreference\":\"HYBRID\",\"locations\":[\"Bengaluru\"]}"
        );

        JobSemanticEnrichment job = buildJob(
                "ENGINEERING", "SENIOR", 5, null,
                Arrays.asList("Java", "Spring Boot"),
                Arrays.asList("Docker"),
                "Bachelor's degree", "HYBRID", "[\"Bengaluru\"]"
        );

        JobMatchResult result1 = matchingService.calculateMatch(candidate, job);
        JobMatchResult result2 = matchingService.calculateMatch(candidate, job);

        System.out.println("\n=== REPRODUCIBILITY ===");
        System.out.println("Run 1: " + result1.getOverallScore());
        System.out.println("Run 2: " + result2.getOverallScore());

        assertEquals(result1.getOverallScore(), result2.getOverallScore(), "Same inputs must produce same score");
        assertEquals(result1.getSkillScore(), result2.getSkillScore());
        assertEquals(result1.getExperienceScore(), result2.getExperienceScore());
    }

    // ─── Test 10: Empty/null edge cases ─────────────────────────────────
    @Test
    public void testEdgeCases() throws Exception {
        CandidateSemanticProfile candidate = buildCandidate(
                null, null, null,
                Collections.emptyList(), null,
                null
        );

        JobSemanticEnrichment job = buildJob(
                null, null, null, null,
                Collections.emptyList(), Collections.emptyList(),
                null, null, null
        );

        JobMatchResult result = matchingService.calculateMatch(candidate, job);

        System.out.println("\n=== EDGE CASE: ALL NULL ===");
        System.out.println(result.getExplanation());
        System.out.println("Overall: " + result.getOverallScore());

        assertNotNull(result);
        assertTrue(result.getOverallScore() >= 0);
        assertTrue(result.getOverallScore() <= 100);
        assertNotNull(result.getConfidence());
        assertEquals("LOW", result.getConfidence());
    }

    // ─── Test 11: Score boundaries ──────────────────────────────────────
    @Test
    public void testScoreBoundaries() throws Exception {
        // Perfect match
        CandidateSemanticProfile perfect = buildCandidate(
                "Software Engineer", "EXPERIENCED", 5,
                Arrays.asList("Java", "Spring Boot", "SQL"),
                "[{\"degree\":\"B.Tech\",\"fieldOfStudy\":\"Computer Science\"}]",
                "{\"remotePreference\":\"REMOTE\",\"locations\":[\"Bengaluru\"]}"
        );

        JobSemanticEnrichment jobPerfect = buildJob(
                "ENGINEERING", "EXPERIENCED", 3, 8,
                Arrays.asList("Java", "Spring Boot", "SQL"),
                Collections.emptyList(),
                "Bachelor's degree in Computer Science", "REMOTE", "[\"Bengaluru\"]"
        );

        JobMatchResult result = matchingService.calculateMatch(perfect, jobPerfect);

        System.out.println("\n=== SCORE BOUNDARIES ===");
        System.out.println("Perfect match: " + result.getOverallScore());

        assertTrue(result.getOverallScore() >= 0 && result.getOverallScore() <= 100);
        assertTrue(result.getSkillScore() >= 0 && result.getSkillScore() <= 100);
        assertTrue(result.getExperienceScore() >= 0 && result.getExperienceScore() <= 100);
        assertTrue(result.getCareerLevelScore() >= 0 && result.getCareerLevelScore() <= 100);
        assertTrue(result.getJobFamilyScore() >= 0 && result.getJobFamilyScore() <= 100);
        assertTrue(result.getEducationScore() >= 0 && result.getEducationScore() <= 100);
        assertTrue(result.getLocationScore() >= 0 && result.getLocationScore() <= 100);
        assertTrue(result.getWorkModeScore() >= 0 && result.getWorkModeScore() <= 100);

        // Verify no NaN
        assertFalse(Double.isNaN(result.getOverallScore()));
    }
}
