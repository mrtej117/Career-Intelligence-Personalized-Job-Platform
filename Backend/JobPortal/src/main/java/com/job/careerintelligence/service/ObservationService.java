package com.job.careerintelligence.service;

import com.job.careerintelligence.dto.RawJobDTO;
import com.job.careerintelligence.dto.SaveObservationResult;
import com.job.careerintelligence.dto.SaveObservationResult.ObservationStatus;
import com.job.careerintelligence.entity.RawJobObservation;
import com.job.careerintelligence.entity.SourceJobIdentity;
import com.job.careerintelligence.repository.RawJobObservationRepository;
import com.job.careerintelligence.repository.SourceJobIdentityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObservationService {
    
    private final SourceJobIdentityRepository identityRepository;
    private final RawJobObservationRepository observationRepository;

    @Transactional
    public SaveObservationResult saveObservation(RawJobDTO dto) {
        String contentHash = computeHash(dto.getRawJson());
        LocalDateTime now = LocalDateTime.now();

        Optional<SourceJobIdentity> existingIdentity = identityRepository.findBySourcePlatformAndCompanyNameAndExternalJobId(
            dto.getSourcePlatform(), dto.getCompanyName(), dto.getExternalJobId()
        );

        SourceJobIdentity identity;
        ObservationStatus status = ObservationStatus.NEW;

        if (existingIdentity.isPresent()) {
            identity = existingIdentity.get();
            identity.setLastSeenAt(now);
            identity.setIsActive(true);
            identity.setMissingCyclesCount(0); // Reset missing cycles
            identityRepository.save(identity);

            Optional<RawJobObservation> latestObsOpt = observationRepository.findFirstByJobIdentityIdOrderByCollectionTimestampDesc(identity.getId());
            if (latestObsOpt.isPresent()) {
                RawJobObservation latestObs = latestObsOpt.get();
                if (contentHash.equals(latestObs.getContentHash())) {
                    log.info("UNCHANGED JOB - SKIPPED ENRICHMENT: {}", dto.getExternalJobId());
                    return new SaveObservationResult(latestObs, ObservationStatus.UNCHANGED);
                } else {
                    status = ObservationStatus.CHANGED;
                }
            }
        } else {
            identity = new SourceJobIdentity();
            identity.setSourcePlatform(dto.getSourcePlatform());
            identity.setCompanyName(dto.getCompanyName());
            identity.setExternalJobId(dto.getExternalJobId());
            identity.setFirstSeenAt(now);
            identity.setLastSeenAt(now);
            identity.setIsActive(true);
            identity.setMissingCyclesCount(0);
            identity = identityRepository.save(identity);
        }

        RawJobObservation observation = new RawJobObservation();
        observation.setJobIdentity(identity);
        observation.setCollectionTimestamp(now);
        observation.setSourceJobUrl(dto.getSourceJobUrl());
        observation.setListingUrl(dto.getListingUrl());
        observation.setRawTitle(dto.getRawTitle());
        observation.setRawLocation(dto.getRawLocation());
        observation.setRawDepartment(dto.getRawDepartment());
        observation.setRawEmploymentType(dto.getRawEmploymentType());
        observation.setRawExperience(dto.getRawExperience());
        observation.setRawQualifications(dto.getRawQualifications());
        observation.setRawResponsibilities(dto.getRawResponsibilities());
        observation.setRawSkills(dto.getRawSkills());
        observation.setRawJson(dto.getRawJson());
        observation.setRawHtml(dto.getRawHtml());
        observation.setContentHash(contentHash);

        return new SaveObservationResult(observationRepository.save(observation), status);
    }
    
    @Transactional
    public int processMissingJobs(String platform, String company, List<String> activeExternalIdsFound, int missingThreshold) {
        List<SourceJobIdentity> activeJobs = identityRepository.findBySourcePlatformAndCompanyNameAndIsActiveTrue(platform, company);
        int newlyInactiveCount = 0;
        
        for (SourceJobIdentity job : activeJobs) {
            if (!activeExternalIdsFound.contains(job.getExternalJobId())) {
                job.setMissingCyclesCount(job.getMissingCyclesCount() + 1);
                if (job.getMissingCyclesCount() >= missingThreshold) {
                    job.setIsActive(false);
                    newlyInactiveCount++;
                    log.info("Job {} marked INACTIVE after {} missing cycles", job.getExternalJobId(), job.getMissingCyclesCount());
                } else {
                    log.info("Job {} missing for {} cycles. Threshold is {}", job.getExternalJobId(), job.getMissingCyclesCount(), missingThreshold);
                }
                identityRepository.save(job);
            }
        }
        
        return newlyInactiveCount;
    }

    private String computeHash(String data) {
        if (data == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to compute hash", e);
        }
    }
}
