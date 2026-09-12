package com.job.careerintelligence;

import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.repository.*;
import com.job.careerintelligence.service.DataCollectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class FinalAuditTest {

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private CareerSystemRegistryRepository registryRepo;

    @Autowired
    private ResearchRecordRepository researchRepo;

    @Autowired
    private RawJobObservationRepository observationRepo;

    @Autowired
    private UniversalJobRepresentationRepository universalRepo;

    @Test
    @Transactional
    public void executeFinalAudit() {
        // Setup companies to run the test
        setupCompany("Stripe", "Fintech", "Bengaluru", "Greenhouse");
        setupCompany("Twilio", "Telecom", "Bengaluru", "Greenhouse");
        setupCompany("Inmobi", "AdTech", "Bengaluru", "Greenhouse");
        setupCompany("paytm", "Fintech", "Noida", "Lever");
        setupCompany("freshworks", "SaaS", "Chennai", "Lever");
        setupCompany("razorpay", "Fintech", "Bengaluru", "Greenhouse"); // guessing maybe Lever or Greenhouse
        setupCompany("cred", "Fintech", "Bengaluru", "Greenhouse"); // guessing
        setupCompany("meesho", "Ecommerce", "Bengaluru", "Greenhouse"); // guessing

        // 1. Run collection cycle
        dataCollectionService.runCollectionCycle();

        List<RawJobObservation> observations = observationRepo.findAll();
        List<UniversalJobRepresentation> universals = universalRepo.findAll();

        System.out.println("==================================================");
        System.out.println("STAGE 1 FINAL AUDIT REPORT");
        System.out.println("==================================================");
        System.out.println("Total Observations Evaluated: " + observations.size());

        // Calculate Fields Coverage
        long titlePop = universals.stream().filter(j -> j.getUniversalTitle() != null && !j.getUniversalTitle().isEmpty()).count();
        long locPop = universals.stream().filter(j -> j.getNormalizedLocation() != null && !j.getNormalizedLocation().isEmpty()).count();
        long deptPop = universals.stream().filter(j -> j.getDepartment() != null && !j.getDepartment().isEmpty()).count();
        long empPop = universals.stream().filter(j -> j.getEmploymentType() != null && !j.getEmploymentType().isEmpty()).count();
        long qualPop = universals.stream().filter(j -> j.getQualifications() != null && !j.getQualifications().isEmpty()).count();
        long respPop = universals.stream().filter(j -> j.getResponsibilities() != null && !j.getResponsibilities().isEmpty()).count();
        long reqPop = universals.stream().filter(j -> j.getRequiredSkills() != null && !j.getRequiredSkills().isEmpty()).count();
        long eduPop = universals.stream().filter(j -> j.getEducation() != null && !j.getEducation().isEmpty()).count();
        long salPop = universals.stream().filter(j -> j.getSalary() != null && !j.getSalary().isEmpty()).count();

        System.out.println("\nFIELD COVERAGE STATISTICS:");
        System.out.println("TITLE: " + titlePop + " / " + universals.size() + " populated (" + (universals.size() > 0 ? (titlePop * 100 / universals.size()) : 0) + "%)");
        System.out.println("LOCATION: " + locPop + " / " + universals.size() + " populated (" + (universals.size() > 0 ? (locPop * 100 / universals.size()) : 0) + "%)");
        System.out.println("DEPARTMENT: " + deptPop + " / " + universals.size() + " populated (" + (universals.size() > 0 ? (deptPop * 100 / universals.size()) : 0) + "%)");
        System.out.println("EMPLOYMENT_TYPE: " + empPop + " / " + universals.size() + " populated (" + (universals.size() > 0 ? (empPop * 100 / universals.size()) : 0) + "%)");
        System.out.println("QUALIFICATIONS: " + qualPop + " / " + universals.size() + " populated (" + (universals.size() > 0 ? (qualPop * 100 / universals.size()) : 0) + "%)");
        System.out.println("RESPONSIBILITIES: " + respPop + " / " + universals.size() + " populated (" + (universals.size() > 0 ? (respPop * 100 / universals.size()) : 0) + "%)");
        System.out.println("REQUIRED_SKILLS: " + reqPop + " / " + universals.size() + " populated (" + (universals.size() > 0 ? (reqPop * 100 / universals.size()) : 0) + "%)");
        System.out.println("EDUCATION: " + eduPop + " / " + universals.size() + " populated (" + (universals.size() > 0 ? (eduPop * 100 / universals.size()) : 0) + "%)");
        System.out.println("SALARY: " + salPop + " / " + universals.size() + " populated (" + (universals.size() > 0 ? (salPop * 100 / universals.size()) : 0) + "%)");

        System.out.println("\nJOB FAMILY DISTRIBUTION:");
        universals.stream().map(UniversalJobRepresentation::getJobFamily).distinct().forEach(jf -> {
            long count = universals.stream().filter(j -> jf == null ? j.getJobFamily() == null : jf.equals(j.getJobFamily())).count();
            System.out.println((jf == null ? "NULL" : jf) + ": " + count);
        });

        System.out.println("\nCAREER LEVEL DISTRIBUTION:");
        universals.stream().map(UniversalJobRepresentation::getCareerLevel).distinct().forEach(cl -> {
            long count = universals.stream().filter(j -> cl == null ? j.getCareerLevel() == null : cl.equals(j.getCareerLevel())).count();
            System.out.println((cl == null ? "NULL" : cl) + ": " + count);
        });

        System.out.println("\nEMPLOYMENT TYPE DISTRIBUTION:");
        universals.stream().map(UniversalJobRepresentation::getEmploymentType).distinct().forEach(et -> {
            long count = universals.stream().filter(j -> et == null ? j.getEmploymentType() == null : et.equals(j.getEmploymentType())).count();
            System.out.println((et == null ? "NULL" : et) + ": " + count);
        });

        System.out.println("==================================================");
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
