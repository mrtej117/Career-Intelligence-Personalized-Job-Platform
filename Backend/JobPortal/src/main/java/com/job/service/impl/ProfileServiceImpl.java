package com.job.service.impl;

import com.job.dto.request.UpdateProfileRequestDTO;
import com.job.dto.response.EmployerProfileDTO;
import com.job.dto.response.JobSeekerProfileDTO;
import com.job.entity.Employer;
import com.job.entity.JobSeeker;
import com.job.entity.User;
import com.job.enums.Role;
import com.job.exception.BadRequestException;
import com.job.service.interfaces.IProfileService;
import com.job.repository.UserRepository;
import com.job.exception.ResourceNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements IProfileService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public String uploadResume(MultipartFile file, JobSeeker jobSeeker) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("File size must not exceed 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("application/pdf") &&
                 !contentType.equals("application/msword") &&
                 !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new BadRequestException("Only PDF and Word documents are allowed");
        }
        validateResumeBytes(file, contentType);
        log.info("Uploading resume for user: {}, file: {}", jobSeeker.getUsername(), file.getOriginalFilename());
        String url = cloudinaryService.uploadResume(file);
        jobSeeker.setResumeUrl(url);
        jobSeeker.setResumeOriginalName(file.getOriginalFilename());
        userRepository.save(jobSeeker);
        return url;
    }

    @Override
    @Transactional
    public String uploadProfilePicture(MultipartFile file, User user) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("File size must not exceed 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed");
        }
        log.info("Uploading profile picture for user: {}, file: {}", user.getUsername(), file.getOriginalFilename());
        String url = cloudinaryService.uploadImage(file);
        user.setProfilePictureUrl(url);
        userRepository.save(user);
        return url;
    }

    @Override
    @Transactional
    public void updateJobSeekerProfile(JobSeeker currentUser, UpdateProfileRequestDTO updatedInfo) {
        log.info("Updating profile for job seeker: {}", currentUser.getUsername());
        currentUser.setName(updatedInfo.getName());
        currentUser.setUsername(updatedInfo.getUsername());
        currentUser.setEmail(updatedInfo.getEmail());
        currentUser.setDob(updatedInfo.getDob());
        userRepository.save(currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getCurrentUserDto(User user) {
        if (user.getRole() == Role.JOB_SEEKER && user instanceof JobSeeker jobSeeker) {
            JobSeekerProfileDTO dto = new JobSeekerProfileDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setName(user.getName());
            dto.setRole(user.getRole().name());
            dto.setProfilePicture(user.getProfilePictureUrl());
            dto.setResume(jobSeeker.getResumeUrl());
            String originalName = jobSeeker.getResumeOriginalName();
            if ((originalName == null || originalName.isBlank()) && jobSeeker.getResumeUrl() != null) {
                String ref = jobSeeker.getResumeUrl();
                if (ref.startsWith("local://")) {
                    String raw = ref.substring("local://".length());
                    int underscoreIdx = raw.indexOf('_');
                    originalName = (underscoreIdx > 0 && underscoreIdx < raw.length() - 1) ? raw.substring(underscoreIdx + 1) : raw;
                }
            }
            dto.setResumeOriginalName(originalName);
            dto.setDob(jobSeeker.getDob());
            return dto;
        }

        if (user.getRole() == Role.EMPLOYER && user instanceof Employer employer) {
            EmployerProfileDTO dto = new EmployerProfileDTO();
            dto.setId(employer.getId());
            dto.setUsername(employer.getUsername());
            dto.setEmail(employer.getEmail());
            dto.setName(employer.getName());
            dto.setRole(employer.getRole().name());
            dto.setCompanyName(employer.getCompanyName());
            dto.setIndustry(employer.getIndustry());
            dto.setProfilePicture(employer.getProfilePictureUrl());
            return dto;
        }

        throw new BadRequestException("Unsupported user role");
    }

    private void validateResumeBytes(MultipartFile file, String contentType) {
        byte[] header = new byte[4];
        try (InputStream is = file.getInputStream()) {
            if (is.read(header) < 4) {
                throw new BadRequestException("Only PDF and Word documents are allowed");
            }
        } catch (IOException e) {
            throw new BadRequestException("Only PDF and Word documents are allowed");
        }
        boolean valid = switch (contentType) {
            case "application/pdf" ->
                (header[0] & 0xFF) == 0x25 && (header[1] & 0xFF) == 0x50 &&
                (header[2] & 0xFF) == 0x44 && (header[3] & 0xFF) == 0x46; // %PDF
            case "application/msword" ->
                (header[0] & 0xFF) == 0xD0 && (header[1] & 0xFF) == 0xCF &&
                (header[2] & 0xFF) == 0x11 && (header[3] & 0xFF) == 0xE0; // OLE2
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
                (header[0] & 0xFF) == 0x50 && (header[1] & 0xFF) == 0x4B &&
                (header[2] & 0xFF) == 0x03 && (header[3] & 0xFF) == 0x04; // PK ZIP
            default -> false;
        };
        if (!valid) {
            throw new BadRequestException("Only PDF and Word documents are allowed");
        }
    }

    @Override
    public Resource getResumeResource(JobSeeker jobSeeker) {
        String resumeUrl = jobSeeker.getResumeUrl();
        if (resumeUrl == null || resumeUrl.isBlank()) {
            throw new ResourceNotFoundException("No resume found for user");
        }
        if (resumeUrl.startsWith("local://")) {
            String filename = resumeUrl.substring("local://".length());
            Path filePath = Paths.get("uploads", "resumes").resolve(filename).toAbsolutePath().normalize();
            if (Files.exists(filePath)) {
                return new FileSystemResource(filePath);
            }
            // Fallback: check if file exists matching filename suffix or basename in uploads/resumes
            try {
                Path uploadDir = Paths.get("uploads", "resumes").toAbsolutePath().normalize();
                if (Files.exists(uploadDir)) {
                    try (var stream = Files.list(uploadDir)) {
                        Optional<Path> found = stream.filter(p -> p.getFileName().toString().endsWith(filename) || filename.endsWith(p.getFileName().toString())).findFirst();
                        if (found.isPresent()) {
                            return new FileSystemResource(found.get());
                        }
                    }
                }
            } catch (IOException ignored) {}
            throw new ResourceNotFoundException("Resume file not found on server: " + filename);
        } else {
            try {
                return new UrlResource(resumeUrl);
            } catch (MalformedURLException e) {
                throw new ResourceNotFoundException("Invalid resume URL: " + resumeUrl);
            }
        }
    }
}
