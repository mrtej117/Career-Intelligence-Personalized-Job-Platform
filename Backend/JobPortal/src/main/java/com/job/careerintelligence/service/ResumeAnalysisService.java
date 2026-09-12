package com.job.careerintelligence.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.entity.CandidateResumeObservation;
import com.job.careerintelligence.entity.CandidateSemanticProfile;
import com.job.careerintelligence.llm.LocalLlmClient;
import com.job.careerintelligence.llm.SemanticCandidateAnalysis;
import com.job.careerintelligence.repository.CandidateResumeObservationRepository;
import com.job.careerintelligence.repository.CandidateSemanticProfileRepository;
import com.job.entity.JobSeeker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeAnalysisService {

    private final CandidateResumeObservationRepository obsRepo;
    private final CandidateSemanticProfileRepository profileRepo;
    private final ResumeTextExtractor textExtractor;
    private final LocalLlmClient llmClient;
    private final CandidateProfileValidator validator;
    private final ObjectMapper objectMapper;
    private final EmbeddingService embeddingService;

    @Transactional
    public CandidateSemanticProfile processResume(JobSeeker seeker) {
        String url = seeker.getResumeUrl();
        if (url == null || url.isEmpty()) {
            log.warn("JobSeeker {} has no resume URL", seeker.getId());
            return null;
        }

        String text = textExtractor.extractTextFromUrl(url);
        return doProcessResumeText(seeker, text, url);
    }

    @Transactional
    public CandidateSemanticProfile processResumeLocal(JobSeeker seeker, byte[] fileBytes, String originalFileName) {
        if (fileBytes == null || fileBytes.length == 0) {
            log.warn("JobSeeker {} provided empty local file bytes", seeker.getId());
            return null;
        }

        String text = textExtractor.extractTextFromBytes(fileBytes, originalFileName);
        // Fallback for reference if URL isn't present
        String reference = "local://" + originalFileName;
        if (seeker.getResumeUrl() != null && !seeker.getResumeUrl().isEmpty()) {
            reference = seeker.getResumeUrl();
        }
        return doProcessResumeText(seeker, text, reference);
    }

    private CandidateSemanticProfile doProcessResumeText(JobSeeker seeker, String text, String sourceReference) {
        if (text == null || text.trim().isEmpty()) {
            log.error("Failed to extract text from resume for job seeker {}", seeker.getId());
            return null;
        }

        // 2. Hash text
        String hash = computeHash(text);

        // 3. Check existing
        Optional<CandidateResumeObservation> existingObs = obsRepo.findByJobSeekerIdAndContentHash(seeker.getId(), hash);
        CandidateResumeObservation obs;
        if (existingObs.isPresent()) {
            obs = existingObs.get();
            Optional<CandidateSemanticProfile> existingProfile = profileRepo.findByJobSeekerIdAndContentHash(seeker.getId(), hash);
            if (existingProfile.isPresent()) {
                log.info("Resume for seeker {} already processed. Skipping LLM.", seeker.getId());
                return existingProfile.get();
            }
        } else {
            obs = new CandidateResumeObservation();
            obs.setJobSeekerId(seeker.getId());
            obs.setSourceResumeReference(sourceReference);
            obs.setExtractedText(text);
            obs.setUploadedAt(LocalDateTime.now());
            obs.setContentHash(hash);
            obs.setProcessingStatus("PENDING");
            obs = obsRepo.save(obs);
        }

        // 4. Send to LLM
        if (!llmClient.isModelAvailable()) {
            log.error("LLM model not available.");
            obs.setProcessingStatus("FAILED");
            obsRepo.save(obs);
            return null;
        }

        obs.setProcessingStatus("PROCESSING");
        obsRepo.save(obs);

        SemanticCandidateAnalysis analysis = llmClient.analyzeResume(text);
        
        if (analysis == null) {
            log.error("LLM returned null for resume of seeker {}", seeker.getId());
            obs.setProcessingStatus("FAILED");
            obsRepo.save(obs);
            return null;
        }

        // 5. Validate
        analysis = validator.validateAndSanitize(analysis);

        // 6. Save Profile
        // Mark old versions as not latest
        profileRepo.findTopByJobSeekerIdAndIsLatestVersionTrueOrderByProcessingTimestampDesc(seeker.getId())
                .ifPresent(old -> {
                    old.setIsLatestVersion(false);
                    profileRepo.save(old);
                });

        CandidateSemanticProfile profile = new CandidateSemanticProfile();
        profile.setJobSeekerId(seeker.getId());
        profile.setResumeObservationId(obs.getId());
        profile.setModelName(llmClient.getModelName());
        profile.setProcessingTimestamp(LocalDateTime.now());
        profile.setProcessingStatus("COMPLETED");
        profile.setContentHash(hash);
        profile.setIsLatestVersion(true);
        
        profile.setProfessionalTitle(analysis.getProfessionalTitle());
        profile.setCareerLevel(analysis.getCareerLevel());
        profile.setTotalExperienceYears(analysis.getTotalExperienceYears());
        
        try {
            profile.setSkillsJson(objectMapper.writeValueAsString(analysis.getTechnicalSkills()));
            profile.setExperienceJson(objectMapper.writeValueAsString(analysis.getEmployers()));
            profile.setEducationJson(objectMapper.writeValueAsString(analysis.getEducation()));
            profile.setProjectsJson(objectMapper.writeValueAsString(analysis.getProjects()));
            profile.setCertificationsJson(objectMapper.writeValueAsString(analysis.getCertifications()));
            
            // create a location/remote DTO mapping
            String locationStr = objectMapper.writeValueAsString(analysis.getLocationsMentioned());
            profile.setLocationPreferencesJson("{\"remotePreference\":\"" + analysis.getRemotePreference() + "\",\"locations\":" + locationStr + "}");
            profile.setAmbiguousFieldsJson(objectMapper.writeValueAsString(analysis.getAmbiguousFields()));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize JSON for profile", e);
        }
        
        obs.setProcessedAt(LocalDateTime.now());
        obs.setProcessingStatus("COMPLETED");
        obsRepo.save(obs);

        CandidateSemanticProfile savedProfile = profileRepo.save(profile);

        // Generate semantic embedding text
        StringBuilder semText = new StringBuilder();
        semText.append(analysis.getProfessionalTitle() != null ? analysis.getProfessionalTitle() : "").append(" ");
        semText.append(analysis.getCareerLevel() != null ? analysis.getCareerLevel() : "").append(" ");
        if (analysis.getTechnicalSkills() != null) semText.append(String.join(", ", analysis.getTechnicalSkills())).append(" ");
        if (analysis.getTools() != null) semText.append(String.join(", ", analysis.getTools())).append(" ");
        if (analysis.getFrameworks() != null) semText.append(String.join(", ", analysis.getFrameworks())).append(" ");
        if (analysis.getProgrammingLanguages() != null) semText.append(String.join(", ", analysis.getProgrammingLanguages())).append(" ");

        embeddingService.generateAndSaveEmbedding("CANDIDATE", savedProfile.getId(), semText.toString().trim());

        return savedProfile;
    }

    private String computeHash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
