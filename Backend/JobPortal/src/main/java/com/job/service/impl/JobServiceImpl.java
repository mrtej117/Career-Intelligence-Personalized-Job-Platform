package com.job.service.impl;

import com.job.dto.request.JobRequestDTO;
import com.job.dto.response.JobResponseDTO;
import com.job.dto.response.PageResponseDTO;
import com.job.entity.Employer;
import com.job.entity.Job;
import com.job.enums.JobType;
import com.job.enums.WorkMode;
import com.job.exception.BadRequestException;
import com.job.exception.ResourceNotFoundException;
import com.job.exception.UnauthorizedException;
import com.job.repository.JobRepository;
import com.job.service.interfaces.IJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements IJobService {

    private final JobRepository jobRepository;

    @Override
    @Transactional
    public Job createJob(JobRequestDTO dto, Employer employer) {
        log.info("Creating job '{}' for employer: {}", dto.getTitle(), employer.getUsername());
        Job job = new Job();

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setType(dto.getType() != null ? dto.getType() : JobType.FULL_TIME); // Default type
        job.setWorkMode(dto.getWorkMode() != null ? dto.getWorkMode() : WorkMode.HYBRID); // Default work mode
        job.setResponsibilities(dto.getResponsibilities());
        job.setRequiredSkills(dto.getRequiredSkills());
        job.setScreeningQuestions(dto.getScreeningQuestions()); // Optional field
        job.setPostedAt(LocalDateTime.now());
        job.setEmployer(employer);

        return jobRepository.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<JobResponseDTO> getAllJobsSortedByDate(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("postedAt").descending());
        return toPageResponse(jobRepository.findAllWithEmployer(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<JobResponseDTO> searchByTitle(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return toPageResponse(jobRepository.findByTitleContainingIgnoreCaseWithEmployer(keyword, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<JobResponseDTO> searchByType(String type, int page, int size) {
        try {
            JobType jobType = JobType.valueOf(type.toUpperCase());
            Pageable pageable = PageRequest.of(page, size);
            return toPageResponse(jobRepository.findByTypeWithEmployer(jobType, pageable));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid job type provided: {}", type);
            throw new BadRequestException("Invalid job type: " + type);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<JobResponseDTO> searchByLocation(String location, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return toPageResponse(jobRepository.findByLocationContainingIgnoreCaseWithEmployer(location, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<JobResponseDTO> searchByWorkMode(String workMode, int page, int size) {
        try {
            WorkMode mode = WorkMode.valueOf(workMode.toUpperCase());
            Pageable pageable = PageRequest.of(page, size);
            return toPageResponse(jobRepository.findByWorkModeWithEmployer(mode, pageable));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid work mode provided: {}", workMode);
            throw new BadRequestException("Invalid work mode: " + workMode);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponseDTO getJobById(Long id) {
        Job job = jobRepository.findByIdWithEmployer(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return mapToDTO(job);
    }

    @Override
    @Transactional
    public JobResponseDTO updateJob(Long id, JobRequestDTO dto, Employer employer) {
        log.info("Updating job id: {} by employer: {}", id, employer.getUsername());
        Job job = jobRepository.findByIdWithEmployer(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new UnauthorizedException("You are not authorized to update this job");
        }

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setType(dto.getType());
        job.setWorkMode(dto.getWorkMode());
        job.setRequiredSkills(dto.getRequiredSkills());
        job.setResponsibilities(dto.getResponsibilities());
        job.setScreeningQuestions(dto.getScreeningQuestions());

        Job updated = jobRepository.save(job);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId, Employer employer) {
        log.info("Deleting job id: {} by employer: {}", jobId, employer.getUsername());
        Job job = jobRepository.findByIdWithEmployer(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this job");
        }

        jobRepository.delete(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponseDTO> getJobsByEmployer(Employer employer) {
        return jobRepository.findByEmployerWithEmployer(employer).stream()
                .map(this::mapToDTO)
                .toList();
    }

    private PageResponseDTO<JobResponseDTO> toPageResponse(Page<Job> page) {
        List<JobResponseDTO> content = page.getContent().stream()
                .map(this::mapToDTO)
                .toList();
        return new PageResponseDTO<>(
                content,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.isLast()
        );
    }

    private JobResponseDTO mapToDTO(Job job) {
        JobResponseDTO dto = new JobResponseDTO();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setLocation(job.getLocation());
        dto.setPostedAt(job.getPostedAt());
        dto.setCompanyName(job.getEmployer().getCompanyName());
        dto.setType(job.getType());
        dto.setWorkMode(job.getWorkMode());
        dto.setProfilePicture(job.getEmployer().getProfilePictureUrl());
        dto.setEmployerId(job.getEmployer().getId());
        dto.setResponsibilities(new ArrayList<>(job.getResponsibilities() != null ? job.getResponsibilities() : List.of()));
        dto.setRequiredSkills(new ArrayList<>(job.getRequiredSkills() != null ? job.getRequiredSkills() : List.of()));
        dto.setScreeningQuestions(new ArrayList<>(job.getScreeningQuestions() != null ? job.getScreeningQuestions() : List.of()));
        return dto;
    }
}
