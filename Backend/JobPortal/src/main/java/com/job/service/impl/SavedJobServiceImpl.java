package com.job.service.impl;

import com.job.dto.response.JobResponseDTO;
import com.job.entity.Job;
import com.job.entity.JobSeeker;
import com.job.exception.DuplicateResourceException;
import com.job.exception.ResourceNotFoundException;
import com.job.repository.JobRepository;
import com.job.repository.JobSeekerRepository;
import com.job.service.interfaces.ISavedJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavedJobServiceImpl implements ISavedJobService {

    private final JobRepository jobRepository;
    private final JobSeekerRepository jobSeekerRepository;

    @Override
    @Transactional
    public void saveJob(JobSeeker jobSeeker, Long jobId) {
        log.info("Job seeker {} saving job id: {}", jobSeeker.getUsername(), jobId);

        JobSeeker freshJobSeeker = jobSeekerRepository.findById(jobSeeker.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (freshJobSeeker.getSavedJobs().contains(job)) {
            throw new DuplicateResourceException("You already saved this job.");
        }

        freshJobSeeker.getSavedJobs().add(job);
    }

    @Override
    @Transactional
    public void unsaveJob(JobSeeker jobSeeker, Long jobId) {
        log.info("Job seeker {} unsaving job id: {}", jobSeeker.getUsername(), jobId);

        JobSeeker freshJobSeeker = jobSeekerRepository.findById(jobSeeker.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!freshJobSeeker.getSavedJobs().contains(job)) {
            throw new ResourceNotFoundException("This job is not in your saved list.");
        }

        freshJobSeeker.getSavedJobs().remove(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponseDTO> getSavedJobs(JobSeeker jobSeeker) {
        JobSeeker freshJobSeeker = jobSeekerRepository.findById(jobSeeker.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found"));

        return freshJobSeeker.getSavedJobs().stream()
                .map(this::mapToDTO)
                .toList();
    }

    private JobResponseDTO mapToDTO(Job job) {
        JobResponseDTO dto = new JobResponseDTO();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setLocation(job.getLocation());
        dto.setPostedAt(job.getPostedAt());
        dto.setCompanyName(job.getEmployer().getCompanyName());
        dto.setProfilePicture(job.getEmployer().getProfilePictureUrl());
        dto.setType(job.getType());
        dto.setWorkMode(job.getWorkMode());
        dto.setResponsibilities(new ArrayList<>(job.getResponsibilities() != null ? job.getResponsibilities() : List.of()));
        dto.setRequiredSkills(new ArrayList<>(job.getRequiredSkills() != null ? job.getRequiredSkills() : List.of()));
        dto.setEmployerId(job.getEmployer().getId());
        return dto;
    }
}