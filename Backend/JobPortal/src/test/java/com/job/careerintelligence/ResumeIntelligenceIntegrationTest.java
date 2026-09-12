package com.job.careerintelligence;

import com.job.careerintelligence.entity.CandidateResumeObservation;
import com.job.careerintelligence.entity.CandidateSemanticProfile;
import com.job.careerintelligence.repository.CandidateResumeObservationRepository;
import com.job.careerintelligence.repository.CandidateSemanticProfileRepository;
import com.job.careerintelligence.service.ResumeAnalysisService;
import com.job.careerintelligence.service.ResumeTextExtractor;
import com.job.entity.JobSeeker;
import com.job.repository.JobSeekerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("postgres")
@Transactional
class ResumeIntelligenceIntegrationTest {

    @Autowired
    private ResumeAnalysisService resumeAnalysisService;

    @Autowired
    private CandidateSemanticProfileRepository profileRepo;

    @Autowired
    private CandidateResumeObservationRepository obsRepo;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @MockBean
    private ResumeTextExtractor textExtractor;

    private JobSeeker testSeeker;

    @BeforeEach
    public void setup() {
        JobSeeker seeker = new JobSeeker();
        seeker.setUsername("testseeker_llm");
        seeker.setPassword("password");
        seeker.setEmail("testseeker_llm@example.com");
        seeker.setName("Jane Doe");
        seeker.setRole(com.job.enums.Role.JOB_SEEKER);
        seeker.setDob(LocalDate.of(1995, 1, 1));
        seeker.setResumeUrl("http://cloudinary.com/dummy.pdf");
        
        testSeeker = jobSeekerRepository.save(seeker);
    }

    @Test
    public void testFullResumePipeline() {
        // Mock the text extraction to return a synthetic resume
        String syntheticResume = 
                "Jane Doe\n" +
                "Software Engineer\n" +
                "Location: Bengaluru, India\n" +
                "Email: jane.doe@example.com\n\n" +
                "EXPERIENCE\n" +
                "Senior Backend Engineer at TechCorp (2020 - Present)\n" +
                "- Developed REST APIs using Spring Boot and Java.\n" +
                "- Managed PostgreSQL databases and optimized queries.\n" +
                "- Deployed microservices using Docker and Kubernetes.\n\n" +
                "Software Developer at WebSolutions (2017 - 2020)\n" +
                "- Maintained legacy applications in Node.js.\n" +
                "- Built frontend components using React.\n\n" +
                "EDUCATION\n" +
                "B.Tech in Computer Science, NIT Trichy (2013 - 2017)\n\n" +
                "SKILLS\n" +
                "Java, Spring Boot, PostgreSQL, Docker, Kubernetes, React, Node.js, AWS\n\n" +
                "CERTIFICATIONS\n" +
                "AWS Certified Solutions Architect";

        when(textExtractor.extractTextFromUrl(anyString())).thenReturn(syntheticResume);

        long start = System.currentTimeMillis();
        CandidateSemanticProfile profile = resumeAnalysisService.processResume(testSeeker);
        long duration = System.currentTimeMillis() - start;
        
        System.out.println("Processing took: " + duration + "ms");

        assertNotNull(profile, "Profile should not be null");
        assertEquals("COMPLETED", profile.getProcessingStatus());
        assertNotNull(profile.getContentHash());
        
        // Assert some AI extractions
        assertTrue(profile.getSkillsJson().contains("Spring Boot"));
        assertTrue(profile.getExperienceJson().contains("TechCorp"));
        assertTrue(profile.getEducationJson().contains("NIT Trichy"));
        
        // Assert Enums are valid and mapped
        assertNotNull(profile.getCareerLevel()); // e.g. EXPERIENCED or SENIOR

        // Validate observation was stored
        Optional<CandidateResumeObservation> obs = obsRepo.findById(profile.getResumeObservationId());
        assertTrue(obs.isPresent());
        assertEquals(syntheticResume, obs.get().getExtractedText());
        assertEquals(obs.get().getContentHash(), profile.getContentHash());
        
        // Test Versioning: Processing identical resume should return same profile without duplicate run
        CandidateSemanticProfile profile2 = resumeAnalysisService.processResume(testSeeker);
        assertEquals(profile.getId(), profile2.getId(), "Should return same profile if hash matches");
        
        // Test Hallucination resistance with sparse resume
        String sparseResume = "John Smith. Email: john@test.com. Experience: 2 years in retail.";
        when(textExtractor.extractTextFromUrl(anyString())).thenReturn(sparseResume);
        
        CandidateSemanticProfile sparseProfile = resumeAnalysisService.processResume(testSeeker);
        assertNotEquals(profile.getId(), sparseProfile.getId(), "New hash should create new profile");
        
        assertFalse(sparseProfile.getSkillsJson().contains("Spring Boot"), "Should not hallucinate skills");
        assertFalse(sparseProfile.getExperienceJson().contains("TechCorp"), "Should not hallucinate employer");
    }
}
