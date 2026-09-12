package com.job.careerintelligence.llm;

import com.job.careerintelligence.service.CandidateProfileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class CandidateProfileValidatorTest {

    private CandidateProfileValidator validator;

    @BeforeEach
    public void setup() {
        validator = new CandidateProfileValidator();
    }

    @Test
    public void testEnumSanitization() {
        SemanticCandidateAnalysis analysis = new SemanticCandidateAnalysis();
        analysis.setCareerLevel("CEO"); // invalid
        analysis.setRemotePreference("OFFICE"); // invalid

        SemanticCandidateAnalysis sanitized = validator.validateAndSanitize(analysis);
        
        assertNull(sanitized.getCareerLevel());
        assertEquals("UNKNOWN", sanitized.getRemotePreference());
    }

    @Test
    public void testNumericSanitization() {
        SemanticCandidateAnalysis analysis = new SemanticCandidateAnalysis();
        analysis.setTotalExperienceYears(-5);

        SemanticCandidateAnalysis sanitized = validator.validateAndSanitize(analysis);
        
        assertEquals(0, sanitized.getTotalExperienceYears());
    }

    @Test
    public void testArrayCleanup() {
        SemanticCandidateAnalysis analysis = new SemanticCandidateAnalysis();
        analysis.setTechnicalSkills(Arrays.asList("Java", "", null, "   ", "Spring"));

        SemanticCandidateAnalysis sanitized = validator.validateAndSanitize(analysis);
        
        assertEquals(2, sanitized.getTechnicalSkills().size());
        assertTrue(sanitized.getTechnicalSkills().contains("Java"));
        assertTrue(sanitized.getTechnicalSkills().contains("Spring"));
    }
}
