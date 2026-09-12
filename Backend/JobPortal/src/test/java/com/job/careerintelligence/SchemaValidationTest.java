package com.job.careerintelligence;

import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.repository.*;
import com.job.careerintelligence.service.DataCollectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SchemaValidationTest {

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private UniversalJobRepresentationRepository universalRepo;

    @Autowired
    private EdgeCaseReportRepository edgeCaseRepo;

    @Autowired
    private SourceJobIdentityRepository identityRepo;

    @Autowired
    private RawJobObservationRepository observationRepo;

    @Autowired
    private CareerSystemRegistryRepository registryRepo;

    @Autowired
    private ResearchRecordRepository researchRepo;

    @Test
    @org.springframework.transaction.annotation.Transactional
    public void generateValidationReport() {
        // Setup companies to run the test
        setupCompany("Stripe", "Fintech", "Bengaluru", "Greenhouse");
        setupCompany("Gitlab", "DevOps/Tech", "Remote/India", "Greenhouse");
        setupCompany("Twilio", "Telecom", "Bengaluru", "Greenhouse");
        setupCompany("Airbnb", "Hospitality", "Bengaluru", "Greenhouse");
        setupCompany("Roblox", "Gaming", "Bengaluru", "Greenhouse");
        setupCompany("Coinbase", "Crypto", "Remote/India", "Greenhouse");
        setupCompany("Inmobi", "AdTech", "Bengaluru", "Greenhouse");

        dataCollectionService.runCollectionCycle();

        List<UniversalJobRepresentation> jobs = universalRepo.findAll();
        List<EdgeCaseReport> edgeCases = edgeCaseRepo.findAll();
        long total = jobs.size();

        System.out.println("==================================================");
        System.out.println("SCHEMA VALIDATION REPORT");
        System.out.println("==================================================");
        System.out.println("Dataset Used: 7 Live Companies via Greenhouse API");
        System.out.println("Total Observations Evaluated: " + total);

        long titlePop = jobs.stream().filter(j -> j.getUniversalTitle() != null && !j.getUniversalTitle().isEmpty()).count();
        long locPop = jobs.stream().filter(j -> j.getNormalizedLocation() != null && !j.getNormalizedLocation().isEmpty()).count();
        long deptPop = jobs.stream().filter(j -> j.getDepartment() != null && !j.getDepartment().isEmpty()).count();
        long typePop = jobs.stream().filter(j -> j.getEmploymentType() != null && !j.getEmploymentType().isEmpty()).count();
        long qualPop = jobs.stream().filter(j -> j.getQualifications() != null && !j.getQualifications().isEmpty()).count();
        long resPop = jobs.stream().filter(j -> j.getResponsibilities() != null && !j.getResponsibilities().isEmpty()).count();
        long reqSkillsPop = jobs.stream().filter(j -> j.getRequiredSkills() != null && !j.getRequiredSkills().isEmpty()).count();
        long eduPop = jobs.stream().filter(j -> j.getEducation() != null && !j.getEducation().isEmpty()).count();
        long salPop = jobs.stream().filter(j -> j.getSalary() != null && !j.getSalary().isEmpty()).count();

        System.out.println("\nFIELD COVERAGE STATISTICS:");
        System.out.println("TITLE: " + titlePop + " / " + total + " populated (" + (titlePop * 100 / total) + "%)");
        System.out.println("LOCATION: " + locPop + " / " + total + " populated (" + (locPop * 100 / total) + "%)");
        System.out.println("DEPARTMENT: " + deptPop + " / " + total + " populated (" + (deptPop * 100 / total) + "%)");
        System.out.println("EMPLOYMENT_TYPE: " + typePop + " / " + total + " populated (" + (typePop * 100 / total) + "%)");
        System.out.println("QUALIFICATIONS: " + qualPop + " / " + total + " populated (" + (qualPop * 100 / total) + "%)");
        System.out.println("RESPONSIBILITIES: " + resPop + " / " + total + " populated (" + (resPop * 100 / total) + "%)");
        System.out.println("REQUIRED_SKILLS: " + reqSkillsPop + " / " + total + " populated (" + (reqSkillsPop * 100 / total) + "%)");
        System.out.println("EDUCATION: " + eduPop + " / " + total + " populated (" + (eduPop * 100 / total) + "%)");
        System.out.println("SALARY: " + salPop + " / " + total + " populated (" + (salPop * 100 / total) + "%)");

        System.out.println("\nJOB FAMILY DISTRIBUTION:");
        Map<String, Long> familyDist = jobs.stream().collect(Collectors.groupingBy(j -> j.getJobFamily() == null ? "NULL" : j.getJobFamily(), Collectors.counting()));
        familyDist.forEach((k, v) -> System.out.println(k + ": " + v));

        System.out.println("\nCAREER LEVEL DISTRIBUTION:");
        Map<String, Long> levelDist = jobs.stream().collect(Collectors.groupingBy(j -> j.getCareerLevel() == null ? "NULL" : j.getCareerLevel(), Collectors.counting()));
        levelDist.forEach((k, v) -> System.out.println(k + ": " + v));

        System.out.println("\nEDGE CASES RECORDED:");
        Map<String, Long> edgeDist = edgeCases.stream().collect(Collectors.groupingBy(EdgeCaseReport::getIssueType, Collectors.counting()));
        edgeDist.forEach((k, v) -> System.out.println(k + ": " + v));

        System.out.println("==================================================");
        assertTrue(total > 0);
    }

    private void setupCompany(String name, String industry, String city, String platform) {
        ResearchRecord research = new ResearchRecord();
        research.setCompanyName(name);
        research.setIndustry(industry);
        research.setIndianCity(city);
        research.setCareerPlatform(platform);
        research.setResearchStatus("COMPLETED");
        researchRepo.save(research);

        CareerSystemRegistry registry = new CareerSystemRegistry();
        registry.setCompanyName(name);
        registry.setPlatformProvider(platform);
        registry.setObservablePublicRequests(true);
        registryRepo.save(registry);
    }
}
