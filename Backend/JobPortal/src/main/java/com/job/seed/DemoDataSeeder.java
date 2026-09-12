package com.job.seed;

import com.job.entity.*;
import com.job.enums.ApplicationStatus;
import com.job.enums.Role;
import com.job.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Seeds the database with realistic demo data on application startup.
 * <p>
 * Controlled by the property {@code app.seed.enabled=true}.
 * Idempotent: checks for a marker username before seeding.
 * All seeded usernames are prefixed with "seed." for easy identification.
 * <p>
 * Demo credentials — all accounts use password: {@code SeedDemo@2026}
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String SEED_PASSWORD = "SeedDemo@2026";
    private static final String MARKER_USERNAME = "seed.google.employer";

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByUsername(MARKER_USERNAME)) {
            log.info("✅ Seed data already exists — skipping seeder");
            return;
        }

        log.info("🌱 Starting demo data seeding...");
        long start = System.currentTimeMillis();

        String encodedPassword = passwordEncoder.encode(SEED_PASSWORD);

        List<Employer> employers = createEmployers(encodedPassword);
        List<JobSeeker> seekers = createJobSeekers(encodedPassword);
        List<Job> jobs = createJobs(employers);
        List<Application> applications = createApplications(jobs, seekers);
        createSavedJobs(jobs, seekers);
        int notifCount = createNotifications(applications);

        long elapsed = System.currentTimeMillis() - start;
        log.info("🌱 Seeding complete in {}ms: {} employers, {} seekers, {} jobs, {} applications, {} notifications",
                elapsed, employers.size(), seekers.size(), jobs.size(), applications.size(), notifCount);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  EMPLOYERS
    // ═══════════════════════════════════════════════════════════════════

    private List<Employer> createEmployers(String encodedPassword) {
        List<Employer> employers = new ArrayList<>();

        // Real companies
        employers.add(makeEmployer("seed.google.employer", "Google Recruiting", "seed.google@jobportal.local",
                "Google", "Technology", encodedPassword));
        employers.add(makeEmployer("seed.microsoft.employer", "Microsoft Talent", "seed.microsoft@jobportal.local",
                "Microsoft", "Technology", encodedPassword));
        employers.add(makeEmployer("seed.amazon.employer", "Amazon Hiring", "seed.amazon@jobportal.local",
                "Amazon", "E-Commerce & Cloud", encodedPassword));
        employers.add(makeEmployer("seed.apple.employer", "Apple Careers", "seed.apple@jobportal.local",
                "Apple", "Consumer Electronics", encodedPassword));
        employers.add(makeEmployer("seed.meta.employer", "Meta Recruiting", "seed.meta@jobportal.local",
                "Meta", "Social Media & Technology", encodedPassword));
        employers.add(makeEmployer("seed.netflix.employer", "Netflix Talent", "seed.netflix@jobportal.local",
                "Netflix", "Entertainment & Streaming", encodedPassword));
        employers.add(makeEmployer("seed.nvidia.employer", "NVIDIA Hiring", "seed.nvidia@jobportal.local",
                "NVIDIA", "Semiconductors & AI", encodedPassword));
        employers.add(makeEmployer("seed.adobe.employer", "Adobe Careers", "seed.adobe@jobportal.local",
                "Adobe", "Creative Software", encodedPassword));
        employers.add(makeEmployer("seed.salesforce.employer", "Salesforce Talent", "seed.salesforce@jobportal.local",
                "Salesforce", "Enterprise Software", encodedPassword));
        employers.add(makeEmployer("seed.spotify.employer", "Spotify Hiring", "seed.spotify@jobportal.local",
                "Spotify", "Music & Streaming", encodedPassword));
        employers.add(makeEmployer("seed.infosys.employer", "Infosys Recruiting", "seed.infosys@jobportal.local",
                "Infosys", "IT Services & Consulting", encodedPassword));
        employers.add(makeEmployer("seed.tcs.employer", "TCS Talent", "seed.tcs@jobportal.local",
                "TCS", "IT Services & Consulting", encodedPassword));
        employers.add(makeEmployer("seed.accenture.employer", "Accenture Hiring", "seed.accenture@jobportal.local",
                "Accenture", "Consulting & Technology", encodedPassword));
        employers.add(makeEmployer("seed.deloitte.employer", "Deloitte Careers", "seed.deloitte@jobportal.local",
                "Deloitte", "Professional Services", encodedPassword));
        employers.add(makeEmployer("seed.cisco.employer", "Cisco Recruiting", "seed.cisco@jobportal.local",
                "Cisco", "Networking & Security", encodedPassword));

        // Fictional companies
        employers.add(makeEmployer("seed.novastack.employer", "NovaStack HR", "seed.novastack@jobportal.local",
                "NovaStack Technologies", "Cloud Infrastructure", encodedPassword));
        employers.add(makeEmployer("seed.cloudforge.employer", "CloudForge HR", "seed.cloudforge@jobportal.local",
                "CloudForge Labs", "DevOps & Cloud", encodedPassword));
        employers.add(makeEmployer("seed.databridge.employer", "DataBridge HR", "seed.databridge@jobportal.local",
                "DataBridge Systems", "Data Analytics", encodedPassword));
        employers.add(makeEmployer("seed.quantumworks.employer", "QuantumWorks HR", "seed.quantumworks@jobportal.local",
                "QuantumWorks", "AI Research", encodedPassword));
        employers.add(makeEmployer("seed.nexasoft.employer", "NexaSoft HR", "seed.nexasoft@jobportal.local",
                "NexaSoft", "Enterprise Software", encodedPassword));
        employers.add(makeEmployer("seed.vertexai.employer", "VertexAI HR", "seed.vertexai@jobportal.local",
                "VertexAI Labs", "AI & Machine Learning", encodedPassword));
        employers.add(makeEmployer("seed.cybercore.employer", "CyberCore HR", "seed.cybercore@jobportal.local",
                "CyberCore Systems", "Cybersecurity", encodedPassword));
        employers.add(makeEmployer("seed.pixelforge.employer", "PixelForge HR", "seed.pixelforge@jobportal.local",
                "PixelForge Interactive", "Design & Gaming", encodedPassword));
        employers.add(makeEmployer("seed.finedge.employer", "FinEdge HR", "seed.finedge@jobportal.local",
                "FinEdge Technologies", "FinTech", encodedPassword));
        employers.add(makeEmployer("seed.blueorbit.employer", "BlueOrbit HR", "seed.blueorbit@jobportal.local",
                "BlueOrbit Technologies", "SaaS & Cloud", encodedPassword));

        log.info("  → Created {} employer accounts", employers.size());
        return employers;
    }

    private Employer makeEmployer(String username, String name, String email,
                                  String companyName, String industry, String encodedPassword) {
        Employer e = new Employer();
        e.setUsername(username);
        e.setName(name);
        e.setEmail(email);
        e.setCompanyName(companyName);
        e.setIndustry(industry);
        e.setPassword(encodedPassword);
        e.setRole(Role.EMPLOYER);
        
        // Setup logo
        String domain = null;
        if (companyName.equalsIgnoreCase("Google")) domain = "google.com";
        else if (companyName.equalsIgnoreCase("Microsoft")) domain = "microsoft.com";
        else if (companyName.equalsIgnoreCase("Amazon")) domain = "amazon.com";
        else if (companyName.equalsIgnoreCase("Apple")) domain = "apple.com";
        else if (companyName.equalsIgnoreCase("Meta")) domain = "meta.com";
        else if (companyName.equalsIgnoreCase("Netflix")) domain = "netflix.com";
        else if (companyName.equalsIgnoreCase("NVIDIA")) domain = "nvidia.com";
        else if (companyName.equalsIgnoreCase("Adobe")) domain = "adobe.com";
        else if (companyName.equalsIgnoreCase("Salesforce")) domain = "salesforce.com";
        else if (companyName.equalsIgnoreCase("Spotify")) domain = "spotify.com";
        else if (companyName.equalsIgnoreCase("Infosys")) domain = "infosys.com";
        else if (companyName.equalsIgnoreCase("TCS")) domain = "tcs.com";
        else if (companyName.equalsIgnoreCase("Accenture")) domain = "accenture.com";
        else if (companyName.equalsIgnoreCase("Deloitte")) domain = "deloitte.com";
        else if (companyName.equalsIgnoreCase("Cisco")) domain = "cisco.com";
        
        if (domain != null) {
            e.setProfilePictureUrl("https://www.google.com/s2/favicons?domain=" + domain + "&sz=128");
        } else {
            String encodedName = java.net.URLEncoder.encode(companyName, java.nio.charset.StandardCharsets.UTF_8);
            int hash = Math.abs(companyName.hashCode());
            String[] colors = {"2C3E50", "34495E", "16A085", "27AE60", "2980B9", "8E44AD", "2C2C54", "474787", "30336B", "535C68", "6B3F27", "D35400", "C0392B", "7F8C8D"};
            String color = colors[hash % colors.length];
            e.setProfilePictureUrl("https://ui-avatars.com/api/?name=" + encodedName + "&background=" + color + "&color=fff&size=128");
        }
        
        return employerRepository.save(e);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  JOB SEEKERS
    // ═══════════════════════════════════════════════════════════════════

    private List<JobSeeker> createJobSeekers(String encodedPassword) {
        List<JobSeeker> seekers = new ArrayList<>();

        seekers.add(makeJobSeeker("seed.seeker.alice", "Alice Johnson", "seed.alice@jobportal.local",
                LocalDate.of(1996, 3, 15), encodedPassword));
        seekers.add(makeJobSeeker("seed.seeker.bob", "Bob Martinez", "seed.bob@jobportal.local",
                LocalDate.of(1994, 7, 22), encodedPassword));
        seekers.add(makeJobSeeker("seed.seeker.carol", "Carol Chen", "seed.carol@jobportal.local",
                LocalDate.of(1998, 11, 3), encodedPassword));
        seekers.add(makeJobSeeker("seed.seeker.david", "David Patel", "seed.david@jobportal.local",
                LocalDate.of(1995, 1, 28), encodedPassword));
        seekers.add(makeJobSeeker("seed.seeker.emma", "Emma Wilson", "seed.emma@jobportal.local",
                LocalDate.of(1997, 5, 10), encodedPassword));
        seekers.add(makeJobSeeker("seed.seeker.frank", "Frank Okafor", "seed.frank@jobportal.local",
                LocalDate.of(1993, 9, 17), encodedPassword));
        seekers.add(makeJobSeeker("seed.seeker.grace", "Grace Kim", "seed.grace@jobportal.local",
                LocalDate.of(2000, 2, 8), encodedPassword));
        seekers.add(makeJobSeeker("seed.seeker.henry", "Henry Nakamura", "seed.henry@jobportal.local",
                LocalDate.of(1999, 12, 25), encodedPassword));

        log.info("  → Created {} job seeker accounts", seekers.size());
        return seekers;
    }

    private JobSeeker makeJobSeeker(String username, String name, String email,
                                    LocalDate dob, String encodedPassword) {
        JobSeeker s = new JobSeeker();
        s.setUsername(username);
        s.setName(name);
        s.setEmail(email);
        s.setDob(dob);
        s.setPassword(encodedPassword);
        s.setRole(Role.JOB_SEEKER);
        s.setResumeUrl("https://example.com/dummy-resume.pdf");
        
        String encodedName = java.net.URLEncoder.encode(name, java.nio.charset.StandardCharsets.UTF_8);
        int hash = Math.abs(name.hashCode());
        String[] colors = {"2C3E50", "34495E", "16A085", "27AE60", "2980B9", "8E44AD", "2C2C54", "474787", "30336B", "535C68", "6B3F27", "D35400", "C0392B", "7F8C8D"};
        String color = colors[hash % colors.length];
        s.setProfilePictureUrl("https://ui-avatars.com/api/?name=" + encodedName + "&background=" + color + "&color=fff&size=128");
        
        return jobSeekerRepository.save(s);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  JOBS
    // ═══════════════════════════════════════════════════════════════════

    private List<Job> createJobs(List<Employer> employers) {
        List<SeedJobDefinition> definitions = SeedJobDataProvider.allJobs();
        List<Job> jobs = new ArrayList<>();

        for (int i = 0; i < definitions.size(); i++) {
            SeedJobDefinition def = definitions.get(i);
            Employer employer = employers.get(i % employers.size());

            Job job = new Job();
            job.setTitle(def.title());
            job.setDescription(def.description());
            job.setLocation(def.location());
            job.setType(def.type());
            job.setWorkMode(def.workMode());
            job.setPostedAt(LocalDateTime.now().minusDays(def.daysAgo()));
            job.setEmployer(employer);
            job.setResponsibilities(new ArrayList<>(def.responsibilities()));
            job.setRequiredSkills(new ArrayList<>(def.requiredSkills()));
            job.setScreeningQuestions(new ArrayList<>(def.screeningQuestions()));

            jobs.add(jobRepository.save(job));
        }

        log.info("  → Created {} jobs across {} employers", jobs.size(), employers.size());
        return jobs;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  APPLICATIONS
    // ═══════════════════════════════════════════════════════════════════

    private List<Application> createApplications(List<Job> jobs, List<JobSeeker> seekers) {
        List<Application> applications = new ArrayList<>();
        Random rng = new Random(42); // deterministic for reproducibility

        // Status distribution: PENDING=20, REVIEWED=8, INTERVIEW=7, OFFERED=8, REJECTED=7
        ApplicationStatus[] statuses = {
                ApplicationStatus.PENDING, ApplicationStatus.PENDING, ApplicationStatus.PENDING, ApplicationStatus.PENDING,
                ApplicationStatus.REVIEWED, ApplicationStatus.REVIEWED,
                ApplicationStatus.INTERVIEW,
                ApplicationStatus.OFFERED, ApplicationStatus.OFFERED,
                ApplicationStatus.REJECTED
        };

        // Pick ~50 job-seeker pairs (ensuring no duplicate job+seeker combos)
        Set<String> usedPairs = new HashSet<>();
        int targetApplications = 50;
        int attempts = 0;

        while (applications.size() < targetApplications && attempts < 500) {
            attempts++;
            JobSeeker seeker = seekers.get(rng.nextInt(seekers.size()));
            Job job = jobs.get(rng.nextInt(Math.min(jobs.size(), 80))); // apply to first 80 jobs for concentration

            String pairKey = seeker.getId() + ":" + job.getId();
            if (usedPairs.contains(pairKey)) continue;
            usedPairs.add(pairKey);

            ApplicationStatus status = statuses[rng.nextInt(statuses.length)];

            Application app = new Application();
            app.setJob(job);
            app.setJobSeeker(seeker);
            app.setStatus(status);
            // resumeUrl left null — acceptable per schema

            Application saved = applicationRepository.save(app);
            // @PrePersist sets appliedAt to now(); adjust to a realistic date after the job was posted
            int daysAfterPosting = rng.nextInt(5) + 1;
            LocalDateTime appliedDate = job.getPostedAt().plusDays(daysAfterPosting);
            if (appliedDate.isAfter(LocalDateTime.now())) {
                appliedDate = LocalDateTime.now().minusHours(rng.nextInt(48) + 1);
            }
            saved.setAppliedAt(appliedDate);

            applications.add(saved);
        }

        log.info("  → Created {} applications", applications.size());
        return applications;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  SAVED JOBS
    // ═══════════════════════════════════════════════════════════════════

    private void createSavedJobs(List<Job> jobs, List<JobSeeker> seekers) {
        Random rng = new Random(123); // deterministic
        int totalSaved = 0;

        for (JobSeeker seeker : seekers) {
            // Each seeker saves 3-7 random jobs
            int count = rng.nextInt(5) + 3;
            Set<Integer> picked = new HashSet<>();

            while (picked.size() < count && picked.size() < jobs.size()) {
                int idx = rng.nextInt(jobs.size());
                if (picked.add(idx)) {
                    seeker.getSavedJobs().add(jobs.get(idx));
                }
            }

            jobSeekerRepository.save(seeker);
            totalSaved += picked.size();
        }

        log.info("  → Created {} saved job records", totalSaved);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  NOTIFICATIONS
    // ═══════════════════════════════════════════════════════════════════

    private int createNotifications(List<Application> applications) {
        int count = 0;

        for (Application app : applications) {
            ApplicationStatus status = app.getStatus();

            // Create notifications only for status changes (not PENDING)
            if (status == ApplicationStatus.OFFERED ||
                    status == ApplicationStatus.REJECTED ||
                    status == ApplicationStatus.INTERVIEW ||
                    status == ApplicationStatus.REVIEWED) {

                if (count >= 15) break; // cap at ~15 notifications

                String statusLabel = status.name().charAt(0) + status.name().substring(1).toLowerCase();
                String message = String.format("Update: Your application for '%s' at %s has been %s.",
                        app.getJob().getTitle(),
                        app.getJob().getEmployer().getCompanyName(),
                        statusLabel);

                Notification notif = new Notification();
                notif.setMessage(message);
                notif.setRecipient(app.getJobSeeker());
                notif.setApplication(app);
                // createdAt is set by @PrePersist

                notificationRepository.save(notif);
                count++;
            }
        }

        log.info("  → Created {} notifications", count);
        return count;
    }
}
