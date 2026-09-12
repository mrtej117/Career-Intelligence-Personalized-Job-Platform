package com.job.controller;

import com.job.dto.request.UpdateProfileRequestDTO;
import com.job.dto.response.JobResponseDTO;
import com.job.entity.JobSeeker;
import com.job.entity.User;
import com.job.service.interfaces.IProfileService;
import com.job.service.interfaces.ISavedJobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final IProfileService profileService;
    private final ISavedJobService savedJobService;
    private final org.springframework.beans.factory.ObjectProvider<com.job.careerintelligence.service.ResumeAnalysisService> resumeAnalysisServiceProvider;
    private final org.springframework.beans.factory.ObjectProvider<com.job.careerintelligence.matching.RealCandidateJobMatchingService> matchingServiceProvider;
    private final org.springframework.beans.factory.ObjectProvider<com.job.careerintelligence.service.AdaptiveRecommendationService> adaptiveServiceProvider;

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @PostMapping("/jobseeker/upload-resume")
    public ResponseEntity<String> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestAttribute("user") User user
    ) {
        JobSeeker jobSeeker = (JobSeeker) user;
        String resumeUrl = profileService.uploadResume(file, jobSeeker);
        
        // Trigger Career Intelligence asynchronously or synchronously
        try {
            com.job.careerintelligence.service.ResumeAnalysisService resumeAnalysisService = resumeAnalysisServiceProvider.getIfAvailable();
            com.job.careerintelligence.matching.RealCandidateJobMatchingService matchingService = matchingServiceProvider.getIfAvailable();
            
            if (resumeAnalysisService != null) {
                com.job.careerintelligence.entity.CandidateSemanticProfile profile;
                if (resumeUrl.startsWith("local://")) {
                    profile = resumeAnalysisService.processResumeLocal(jobSeeker, file.getBytes(), file.getOriginalFilename());
                } else {
                    profile = resumeAnalysisService.processResume(jobSeeker);
                }
                
                if (profile != null && matchingService != null) {
                    matchingService.matchCandidateToAllJobs(profile.getId());
                }
            }
        } catch (Exception e) {
            // Log and ignore to prevent failing the upload itself
            e.printStackTrace();
        }
        
        return ResponseEntity.ok("Resume uploaded successfully: " + resumeUrl);
    }

    @PostMapping("/upload-profile-picture")
    public ResponseEntity<String> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @RequestAttribute("user") User user
    ) {
        String profileUrl = profileService.uploadProfilePicture(file, user);
        return ResponseEntity.ok("Profile picture uploaded successfully: " + profileUrl);
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @PostMapping("/save-job/{jobId}")
    public ResponseEntity<String> saveJob(@RequestAttribute("user") User user, @PathVariable Long jobId) {
        JobSeeker jobSeeker = (JobSeeker) user;
        savedJobService.saveJob(jobSeeker, jobId);
        try {
            com.job.careerintelligence.service.AdaptiveRecommendationService adaptiveService = adaptiveServiceProvider.getIfAvailable();
            if (adaptiveService != null) {
                adaptiveService.recordAction(jobSeeker.getId(), jobId, null, com.job.careerintelligence.entity.CandidateActionType.SAVE, "saved_jobs_endpoint");
            }
        } catch (Exception e) {
            // Non-blocking log
        }
        return ResponseEntity.ok("Job saved successfully.");
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @PutMapping("/jobseeker/update-profile")
    public ResponseEntity<String> updateProfile(
            @RequestBody @Valid UpdateProfileRequestDTO updatedInfo,
            @RequestAttribute("user") User user
    ) {
        JobSeeker currentUser = (JobSeeker) user;
        profileService.updateJobSeekerProfile(currentUser, updatedInfo);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @DeleteMapping("/unsave-job/{jobId}")
    public ResponseEntity<String> unsaveJob(@RequestAttribute("user") User user, @PathVariable Long jobId) {
        JobSeeker jobSeeker = (JobSeeker) user;
        savedJobService.unsaveJob(jobSeeker, jobId);
        return ResponseEntity.ok("Job removed from saved list.");
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @GetMapping("/saved-jobs")
    public ResponseEntity<List<JobResponseDTO>> getSavedJobs(@RequestAttribute("user") User user) {
        JobSeeker jobSeeker = (JobSeeker) user;
        return ResponseEntity.ok(savedJobService.getSavedJobs(jobSeeker));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestAttribute("user") User user) {
        return ResponseEntity.ok(profileService.getCurrentUserDto(user));
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @GetMapping("/resume/preview")
    public ResponseEntity<Resource> previewResume(@RequestAttribute("user") User user) {
        JobSeeker jobSeeker = (JobSeeker) user;
        Resource resource = profileService.getResumeResource(jobSeeker);
        String filename = jobSeeker.getResumeOriginalName();
        if (filename == null || filename.isBlank()) {
            filename = "resume.pdf";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }
}
