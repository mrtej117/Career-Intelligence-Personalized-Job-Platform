package com.job.careerintelligence.service;

import com.job.careerintelligence.entity.EdgeCaseReport;
import com.job.careerintelligence.entity.FieldObservationMatrix;
import com.job.careerintelligence.entity.RawJobObservation;
import com.job.careerintelligence.entity.UniversalJobRepresentation;
import com.job.careerintelligence.repository.EdgeCaseReportRepository;
import com.job.careerintelligence.repository.FieldObservationMatrixRepository;
import com.job.careerintelligence.repository.UniversalJobRepresentationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NormalizationService {
    
    private final UniversalJobRepresentationRepository universalRepo;
    private final HtmlJobContentParser htmlParser;
    private final FieldObservationMatrixRepository fieldObservationRepo;
    private final EdgeCaseReportRepository edgeCaseRepo;

    @Transactional
    public UniversalJobRepresentation normalize(RawJobObservation raw) {
        UniversalJobRepresentation universal = new UniversalJobRepresentation();
        universal.setRawObservation(raw);
        
        universal.setUniversalTitle(raw.getRawTitle());
        universal.setNormalizedLocation(raw.getRawLocation());
        universal.setDepartment(raw.getRawDepartment());
        universal.setEmploymentType(raw.getRawEmploymentType());
        
        String rawHtml = raw.getRawHtml();
        
        if (rawHtml != null && !rawHtml.isEmpty()) {
            List<HtmlJobContentParser.ParsedSection> sections = htmlParser.parse(rawHtml);
            
            for (HtmlJobContentParser.ParsedSection section : sections) {
                String sourceName = section.getSourceSectionName();
                String lowerName = sourceName.toLowerCase();
                String mappedConcept = null;
                
                if (lowerName.contains("qualifications") && lowerName.contains("preferred")) {
                    universal.getPreferredSkills().addAll(section.getExtractedContent());
                    mappedConcept = "PREFERRED_SKILLS";
                } else if (lowerName.contains("qualifications") && (lowerName.contains("basic") || lowerName.contains("minimum"))) {
                    universal.getQualifications().addAll(section.getExtractedContent());
                    mappedConcept = "QUALIFICATIONS";
                } else if (lowerName.contains("qualifications") || lowerName.contains("requirements") || lowerName.contains("what you'll bring") || lowerName.contains("skills")) {
                    universal.getQualifications().addAll(section.getExtractedContent());
                    mappedConcept = "QUALIFICATIONS";
                } else if (lowerName.contains("responsibilities") || lowerName.contains("what you'll do") || lowerName.contains("role") || lowerName.contains("about the job")) {
                    universal.getResponsibilities().addAll(section.getExtractedContent());
                    mappedConcept = "RESPONSIBILITIES";
                } else if (lowerName.contains("education") || lowerName.contains("degree")) {
                    universal.getEducation().addAll(section.getExtractedContent());
                    mappedConcept = "EDUCATION";
                } else if (lowerName.contains("benefit") || lowerName.contains("perks")) {
                    universal.getBenefits().addAll(section.getExtractedContent());
                    mappedConcept = "BENEFITS";
                } else {
                    mappedConcept = "UNKNOWN";
                }
                
                // Record Field Observation Matrix
                FieldObservationMatrix matrix = new FieldObservationMatrix();
                matrix.setCompanyName(raw.getJobIdentity().getCompanyName());
                matrix.setSourceFieldName(sourceName);
                matrix.setNormalizedConceptCandidate(mappedConcept);
                matrix.setObservedValueType(section.getExtractedContent().isEmpty() ? "EMPTY" : "LIST_OR_PARAGRAPH");
                fieldObservationRepo.save(matrix);
                
                // Edge cases
                if (mappedConcept.equals("UNKNOWN") && !lowerName.contains("uncategorized_intro") && !lowerName.contains("about") && !lowerName.contains("equal")) {
                    EdgeCaseReport edge = new EdgeCaseReport();
                    edge.setRawObservation(raw);
                    edge.setIssueType("AMBIGUOUS_SECTION");
                    edge.setDescription("Section '" + sourceName + "' could not be mapped to a universal concept.");
                    edgeCaseRepo.save(edge);
                }
            }
        }
        
        universal.setCareerLevel(determineCareerLevel(raw.getRawTitle(), rawHtml));
        universal.setJobFamily(determineJobFamily(raw.getRawTitle()));
        
        if ("OTHER".equals(universal.getJobFamily())) {
            EdgeCaseReport edge = new EdgeCaseReport();
            edge.setRawObservation(raw);
            edge.setIssueType("AMBIGUOUS_JOB_FAMILY");
            edge.setDescription("Could not determine job family for title: " + raw.getRawTitle());
            edgeCaseRepo.save(edge);
        }

        if (universal.getCareerLevel() == null) {
            EdgeCaseReport edge = new EdgeCaseReport();
            edge.setRawObservation(raw);
            edge.setIssueType("AMBIGUOUS_CAREER_LEVEL");
            edge.setDescription("Could not determine career level for title: " + raw.getRawTitle());
            edgeCaseRepo.save(edge);
        }
        
        return universalRepo.save(universal);
    }
    
    private String determineCareerLevel(String title, String html) {
        if (title == null) return null;
        String t = title.toLowerCase();
        
        if (t.contains("intern") || t.contains("internship") || t.contains("student")) {
            return "INTERNSHIP";
        } else if (t.contains("apprentice")) {
            return "APPRENTICESHIP";
        } else if (t.contains("graduate") || t.contains("trainee") || t.contains("university")) {
            return "GRADUATE_TRAINEE";
        } else if (t.contains("staff") || t.contains("principal") || t.contains("lead") || t.matches(".*(l[5-9]).*")) {
            return "LEAD";
        } else if (t.contains("senior") || t.contains("sr.") || t.contains("sr ")) {
            return "SENIOR";
        } else if (t.contains("manager") || t.contains("mgr")) {
            return "MANAGER";
        } else if (t.contains("director") || t.contains("head of") || t.contains("vp") || t.contains("vice president")) {
            return "DIRECTOR";
        } else if (t.contains("entry") || t.contains("junior") || t.contains("jr")) {
            return "ENTRY_LEVEL";
        }
        
        // Default to EXPERIENCED if no other keywords found but we have a valid title
        // But only if we are confident. Otherwise return null and let EdgeCase handle it.
        if (t.contains("engineer") || t.contains("developer") || t.contains("analyst") || t.contains("specialist") || t.contains("accountant")) {
             return "EXPERIENCED";
        }
        
        return null;
    }
    
    private String determineJobFamily(String title) {
        if (title == null) return "OTHER";
        String t = title.toLowerCase();
        
        if (t.contains("engineer") || t.contains("developer") || t.contains("architect") || t.contains("programmer")) {
            return "ENGINEERING";
        } else if (t.contains("data") || t.contains("ai") || t.contains("machine learning") || t.contains("ml") || t.contains("analytics")) {
            return "DATA_AI";
        } else if (t.contains("product")) {
            return "PRODUCT";
        } else if (t.contains("design") || t.contains("ux") || t.contains("ui")) {
            return "DESIGN_UX";
        } else if (t.contains("market") || t.contains("brand") || t.contains("growth")) {
            return "MARKETING";
        } else if (t.contains("hr") || t.contains("talent") || t.contains("recruit") || t.contains("people")) {
            return "HR";
        } else if (t.contains("finance") || t.contains("account") || t.contains("tax") || t.contains("audit") || t.contains("payroll")) {
            return "FINANCE";
        } else if (t.contains("sales") || t.contains("account executive") || t.contains("business development")) {
            return "SALES";
        } else if (t.contains("operation") || t.contains("logistics") || t.contains("supply chain")) {
            return "OPERATIONS";
        } else if (t.contains("consult") || t.contains("advisor")) {
            return "CONSULTING";
        } else if (t.contains("manufactur") || t.contains("production")) {
            return "MANUFACTURING";
        } else if (t.contains("research") || t.contains("scientist")) {
            return "RESEARCH";
        }
        return "OTHER";
    }
}
