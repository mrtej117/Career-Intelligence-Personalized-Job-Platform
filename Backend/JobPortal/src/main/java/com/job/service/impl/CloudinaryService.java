package com.job.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.job.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final boolean isLocal;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        this.isLocal = apiKey == null || apiKey.isEmpty() || apiKey.contains("${");
        if (this.isLocal) {
            log.warn("Cloudinary credentials not provided or unresolved. Running in LOCAL mode.");
        }
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public String uploadImage(MultipartFile file) {
        log.info("Uploading image to Cloudinary: {}", file.getOriginalFilename());
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "profile-pictures"
            ));
            String url = (String) result.get("secure_url");
            log.info("Image uploaded successfully to Cloudinary");
            return url;
        } catch (IOException e) {
            log.error("Image upload to Cloudinary failed: {}", e.getMessage());
            throw new BadRequestException("Image upload failed", e);
        }
    }

    public String uploadResume(MultipartFile file) {
        log.info("Uploading resume to Cloudinary: {}", file.getOriginalFilename());
        
        // Always bypass Cloudinary during local development testing as requested by user
        log.info("LOCAL MODE: Bypassing Cloudinary upload. Generating local reference URL.");
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try {
            Path uploadDir = Paths.get("uploads", "resumes").toAbsolutePath().normalize();
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path targetPath = uploadDir.resolve(filename);
            Files.write(targetPath, file.getBytes());
            log.info("Saved local resume file to: {}", targetPath);
        } catch (IOException e) {
            log.error("Failed to save local resume file: {}", e.getMessage(), e);
        }
        return "local://" + filename;
    }

    public void deleteFile(String publicId) {
        log.info("Deleting file from Cloudinary with public id: {}", publicId);
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            log.error("File deletion from Cloudinary failed for public id {}: {}", publicId, e.getMessage());
        }
    }
}
