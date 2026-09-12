package com.job.careerintelligence.llm;

import com.job.careerintelligence.entity.RawJobObservation;
import com.job.careerintelligence.entity.SanitizationAuditRecord;
import com.job.careerintelligence.entity.UniversalJobRepresentation;
import com.job.careerintelligence.repository.SanitizationAuditRecordRepository;
import com.job.careerintelligence.repository.UniversalJobRepresentationRepository;
import com.job.careerintelligence.service.SemanticAnalysisValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SemanticAnalysisValidatorTest {

    private SanitizationAuditRecordRepository auditRepo;
    private UniversalJobRepresentationRepository universalRepo;
    private SemanticAnalysisValidator validator;

    @BeforeEach
    public void setup() {
        auditRepo = mock(SanitizationAuditRecordRepository.class);
        universalRepo = mock(UniversalJobRepresentationRepository.class);
        validator = new SemanticAnalysisValidator(auditRepo, universalRepo);
    }

    @Test
    public void testUnsupportedEnumSanitization() {
        RawJobObservation raw = new RawJobObservation();
        raw.setId(1L);

        UniversalJobRepresentation u = new UniversalJobRepresentation();
        u.setJobFamily("FINANCE");
        u.setCareerLevel("EXPERIENCED");
        when(universalRepo.findByRawObservationId(1L)).thenReturn(u);

        SemanticJobAnalysis analysis = new SemanticJobAnalysis();
        analysis.setJobFamily("ACCOUNTING"); // unsupported
        analysis.setCareerLevel("SUPER_BOSS"); // unsupported

        SemanticJobAnalysis sanitized = validator.validateAndSanitize(analysis, raw, "llama3.2");

        assertEquals("FINANCE", sanitized.getJobFamily());
        assertEquals("EXPERIENCED", sanitized.getCareerLevel());

        verify(auditRepo, times(2)).save(any(SanitizationAuditRecord.class));
    }

    @Test
    public void testNumericSanitization() {
        RawJobObservation raw = new RawJobObservation();
        raw.setId(2L);
        when(universalRepo.findByRawObservationId(2L)).thenReturn(null);

        SemanticJobAnalysis analysis = new SemanticJobAnalysis();
        analysis.setExperienceYearsMin(-5);
        analysis.setExperienceYearsMax(-1);
        analysis.setSalaryMin(100L);
        analysis.setSalaryMax(50L); // min > max

        SemanticJobAnalysis sanitized = validator.validateAndSanitize(analysis, raw, "llama3.2");

        assertEquals(0, sanitized.getExperienceYearsMin());
        assertEquals(0, sanitized.getExperienceYearsMax());
        assertNull(sanitized.getSalaryMin());
        assertNull(sanitized.getSalaryMax());

        verify(auditRepo, atLeast(2)).save(any(SanitizationAuditRecord.class));
    }
    
    @Test
    public void testUnsupportedEnumNoDeterministicFallback() {
        RawJobObservation raw = new RawJobObservation();
        raw.setId(3L);
        // Deterministic returned OTHER and null
        UniversalJobRepresentation u = new UniversalJobRepresentation();
        u.setJobFamily("OTHER");
        u.setCareerLevel(null);
        when(universalRepo.findByRawObservationId(3L)).thenReturn(u);

        SemanticJobAnalysis analysis = new SemanticJobAnalysis();
        analysis.setJobFamily("ASTRONAUT"); // unsupported
        analysis.setCareerLevel("GURU"); // unsupported

        SemanticJobAnalysis sanitized = validator.validateAndSanitize(analysis, raw, "llama3.2");

        assertEquals("OTHER", sanitized.getJobFamily());
        assertNull(sanitized.getCareerLevel());
    }
}
