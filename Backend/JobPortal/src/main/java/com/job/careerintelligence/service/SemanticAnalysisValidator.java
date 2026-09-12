package com.job.careerintelligence.service;

import com.job.careerintelligence.entity.RawJobObservation;
import com.job.careerintelligence.entity.SanitizationAuditRecord;
import com.job.careerintelligence.entity.UniversalJobRepresentation;
import com.job.careerintelligence.llm.SemanticJobAnalysis;
import com.job.careerintelligence.repository.SanitizationAuditRecordRepository;
import com.job.careerintelligence.repository.UniversalJobRepresentationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SemanticAnalysisValidator {

    private final SanitizationAuditRecordRepository auditRepo;
    private final UniversalJobRepresentationRepository universalRepo;

    private static final List<String> VALID_JOB_FAMILIES = Arrays.asList(
            "ENGINEERING", "DATA_AI", "PRODUCT", "DESIGN_UX", "MARKETING",
            "HR", "RECRUITMENT", "FINANCE", "SALES", "OPERATIONS",
            "CONSULTING", "MANUFACTURING", "RESEARCH", "OTHER"
    );

    private static final List<String> VALID_CAREER_LEVELS = Arrays.asList(
            "INTERNSHIP", "APPRENTICESHIP", "GRADUATE_TRAINEE", "ENTRY_LEVEL",
            "EXPERIENCED", "SENIOR", "LEAD", "MANAGER", "DIRECTOR", "SENIOR_LEADERSHIP"
    );

    private static final List<String> VALID_REMOTE_STATUSES = Arrays.asList(
            "REMOTE", "HYBRID", "ONSITE", "UNKNOWN"
    );

    public SemanticJobAnalysis validateAndSanitize(SemanticJobAnalysis analysis, RawJobObservation raw, String modelName) {
        if (analysis == null) return null;
        
        UniversalJobRepresentation deterministic = universalRepo.findByRawObservationId(raw.getId());

        // 1. Validate Job Family
        if (analysis.getJobFamily() != null) {
            String original = analysis.getJobFamily().toUpperCase();
            if (!VALID_JOB_FAMILIES.contains(original)) {
                String fallback = "OTHER";
                if (deterministic != null && deterministic.getJobFamily() != null) {
                    fallback = deterministic.getJobFamily();
                }
                recordAudit(raw.getId(), modelName, "jobFamily", original, fallback, "UNSUPPORTED_ENUM_DETERMINISTIC_EVIDENCE");
                analysis.setJobFamily(fallback);
            } else {
                analysis.setJobFamily(original);
            }
        }

        // 2. Validate Career Level
        if (analysis.getCareerLevel() != null) {
            String original = analysis.getCareerLevel().toUpperCase();
            if (!VALID_CAREER_LEVELS.contains(original)) {
                String fallback = null;
                if (deterministic != null && deterministic.getCareerLevel() != null) {
                    fallback = deterministic.getCareerLevel();
                }
                recordAudit(raw.getId(), modelName, "careerLevel", original, fallback, "UNSUPPORTED_ENUM_FALLBACK");
                analysis.setCareerLevel(fallback);
            } else {
                analysis.setCareerLevel(original);
            }
        }

        // 3. Validate Remote Status
        if (analysis.getRemoteStatus() != null) {
            String original = analysis.getRemoteStatus().toUpperCase();
            if (!VALID_REMOTE_STATUSES.contains(original)) {
                String fallback = "UNKNOWN";
                recordAudit(raw.getId(), modelName, "remoteStatus", original, fallback, "UNSUPPORTED_ENUM_FALLBACK");
                analysis.setRemoteStatus(fallback);
            } else {
                analysis.setRemoteStatus(original);
            }
        }

        // 4. Validate Numeric ranges
        if (analysis.getExperienceYearsMin() != null && analysis.getExperienceYearsMin() < 0) {
            recordAudit(raw.getId(), modelName, "experienceYearsMin", String.valueOf(analysis.getExperienceYearsMin()), "0", "NEGATIVE_VALUE");
            analysis.setExperienceYearsMin(0);
        }
        if (analysis.getExperienceYearsMax() != null && analysis.getExperienceYearsMax() < 0) {
            recordAudit(raw.getId(), modelName, "experienceYearsMax", String.valueOf(analysis.getExperienceYearsMax()), "0", "NEGATIVE_VALUE");
            analysis.setExperienceYearsMax(0);
        }
        if (analysis.getExperienceYearsMin() != null && analysis.getExperienceYearsMax() != null &&
            analysis.getExperienceYearsMin() > analysis.getExperienceYearsMax()) {
            recordAudit(raw.getId(), modelName, "experienceRange", analysis.getExperienceYearsMin()+"-"+analysis.getExperienceYearsMax(), "null", "MIN_GREATER_THAN_MAX");
            analysis.setExperienceYearsMin(null);
            analysis.setExperienceYearsMax(null);
        }

        if (analysis.getSalaryMin() != null && analysis.getSalaryMin() < 0) {
            recordAudit(raw.getId(), modelName, "salaryMin", String.valueOf(analysis.getSalaryMin()), "null", "NEGATIVE_VALUE");
            analysis.setSalaryMin(null);
        }
        if (analysis.getSalaryMax() != null && analysis.getSalaryMax() < 0) {
            recordAudit(raw.getId(), modelName, "salaryMax", String.valueOf(analysis.getSalaryMax()), "null", "NEGATIVE_VALUE");
            analysis.setSalaryMax(null);
        }
        if (analysis.getSalaryMin() != null && analysis.getSalaryMax() != null &&
            analysis.getSalaryMin() > analysis.getSalaryMax()) {
            recordAudit(raw.getId(), modelName, "salaryRange", analysis.getSalaryMin()+"-"+analysis.getSalaryMax(), "null", "MIN_GREATER_THAN_MAX");
            analysis.setSalaryMin(null);
            analysis.setSalaryMax(null);
        }
        
        // 5. Array cleanup (remove empty/null skills)
        if (analysis.getSkills() != null) {
            List<String> clean = analysis.getSkills().stream().filter(s -> s != null && !s.trim().isEmpty()).collect(Collectors.toList());
            analysis.setSkills(clean);
        }
        if (analysis.getPreferredSkills() != null) {
            List<String> clean = analysis.getPreferredSkills().stream().filter(s -> s != null && !s.trim().isEmpty()).collect(Collectors.toList());
            analysis.setPreferredSkills(clean);
        }

        return analysis;
    }

    private void recordAudit(Long rawId, String model, String field, String original, String sanitized, String reason) {
        SanitizationAuditRecord record = new SanitizationAuditRecord();
        record.setRawObservationId(rawId);
        record.setModelName(model);
        record.setTimestamp(LocalDateTime.now());
        record.setField(field);
        record.setOriginalValue(original);
        record.setSanitizedValue(sanitized);
        record.setReason(reason);
        auditRepo.save(record);
    }
}
