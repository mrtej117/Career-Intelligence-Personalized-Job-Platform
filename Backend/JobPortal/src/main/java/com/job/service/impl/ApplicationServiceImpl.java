package com.job.service.impl;

import com.job.designpatterns.Observer.ApplicationObserver;
import com.job.dto.request.ApplicationRequestDTO;
import com.job.dto.response.ApplicationResponseDTO;
import com.job.dto.response.ApplicationViewForEmployerDTO;
import com.job.dto.response.PageResponseDTO;
import com.job.entity.Application;
import com.job.entity.Employer;
import com.job.entity.Job;
import com.job.entity.JobSeeker;
import com.job.enums.ApplicationStatus;
import com.job.exception.BadRequestException;
import com.job.exception.DuplicateResourceException;
import com.job.exception.ResourceNotFoundException;
import com.job.exception.UnauthorizedException;
import com.job.repository.ApplicationRepository;
import com.job.repository.JobRepository;
import com.job.service.interfaces.EmailService;
import com.job.service.interfaces.IApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements IApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final List<ApplicationObserver> observers;
    private final EmailService emailService;

    @Override
    @Transactional
    public ApplicationResponseDTO applyToJob(ApplicationRequestDTO dto, JobSeeker jobSeeker) {
        log.info("Job seeker {} applying to job id: {}", jobSeeker.getUsername(), dto.getJobId());
        if (jobSeeker.getResumeUrl() == null || jobSeeker.getResumeUrl().isBlank()) {
            throw new BadRequestException("You must upload a resume before applying to a job.");
        }

        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        boolean alreadyApplied = applicationRepository.existsByJobAndJobSeeker(job, jobSeeker);
        if (alreadyApplied) {
            throw new DuplicateResourceException("You have already applied for this job");
        }

        List<String> screeningQs = job.getScreeningQuestions();
        if (screeningQs != null && !screeningQs.isEmpty()) {
            if (dto.getScreeningAnswers() == null || dto.getScreeningAnswers().size() != screeningQs.size()) {
                throw new BadRequestException("You must answer all required screening questions.");
            }
        }

        Application application = new Application();
        application.setJob(job);
        application.setJobSeeker(jobSeeker);
        application.setResumeUrl(jobSeeker.getResumeUrl());
        application.setStatus(ApplicationStatus.PENDING);

        applicationRepository.save(application);

        emailService.sendApplicationConfirmation(
                jobSeeker.getEmail(),
                jobSeeker.getName(),
                job.getTitle(),
                job.getEmployer().getCompanyName()
        );

        return mapToDTO(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationViewForEmployerDTO> getAllApplicationsForEmployer(Employer employer, ApplicationStatus status) {
        List<Application> applications;

        if (status == null) {
            applications = applicationRepository.findByJob_Employer(employer);
        } else {
            applications = applicationRepository.findByJob_EmployerAndStatus(employer, status);
        }

        return applications.stream()
                .map(this::mapToEmployerDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ApplicationResponseDTO> getMyApplications(JobSeeker jobSeeker, int page, int size) {
        Page<Application> result = applicationRepository.findByJobSeeker(jobSeeker, PageRequest.of(page, size));
        List<ApplicationResponseDTO> content = result.getContent().stream()
                .map(this::mapToDTO)
                .toList();
        return new PageResponseDTO<>(content, result.getNumber(), result.getTotalPages(),
                result.getTotalElements(), result.getSize(), result.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponseDTO getApplicationById(Long id, JobSeeker requester) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!app.getJobSeeker().getId().equals(requester.getId())) {
            throw new UnauthorizedException("Unauthorized access to application");
        }

        return mapToDTO(app);
    }

    @Override
    @Transactional
    public void withdrawApplication(Long id, JobSeeker requester) {
        log.info("Job seeker {} withdrawing application id: {}", requester.getUsername(), id);
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!app.getJobSeeker().getId().equals(requester.getId())) {
            throw new UnauthorizedException("Unauthorized to withdraw this application");
        }

        applicationRepository.delete(app);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationViewForEmployerDTO> getApplicationsForJob(Long jobId, Employer employer) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new UnauthorizedException("Unauthorized to view applications for this job");
        }

        List<Application> applications = applicationRepository.findByJob(job);

        return applications.stream()
                .map(this::mapToEmployerDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationViewForEmployerDTO getApplicationViewForEmployer(Long id, Employer employer) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!app.getJob().getEmployer().getId().equals(employer.getId())) {
            throw new UnauthorizedException("Unauthorized to view this application");
        }

        return mapToEmployerDTO(app);
    }

    @Override
    @Transactional
    public void updateApplicationStatus(Long applicationId, ApplicationStatus newStatus, Employer employer) {
        log.info("Employer {} updating application id: {} to status: {}", employer.getUsername(), applicationId, newStatus);
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!app.getJob().getEmployer().getId().equals(employer.getId())) {
            throw new UnauthorizedException("Unauthorized to update this application");
        }

        app.setStatus(newStatus);
        applicationRepository.save(app);
        notifyObservers(app.getJobSeeker(), app);

        emailService.sendApplicationStatusUpdate(
                app.getJobSeeker().getEmail(),
                app.getJobSeeker().getName(),
                app.getJob().getTitle(),
                newStatus.toString()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserAppliedToJob(Long jobId, JobSeeker jobSeeker) {
        return applicationRepository.existsByJobIdAndJobSeeker(jobId, jobSeeker);
    }

    private ApplicationResponseDTO mapToDTO(Application application) {
        ApplicationResponseDTO dto = new ApplicationResponseDTO();

        dto.setApplicationId(application.getId());
        dto.setUsername(application.getJobSeeker().getUsername());
        dto.setStatus(application.getStatus());
        dto.setAppliedAt(application.getAppliedAt());

        Job job = application.getJob();
        dto.setJobId(job.getId());
        dto.setJobTitle(job.getTitle());
        dto.setJobType(job.getType().toString());
        dto.setWorkMode(job.getWorkMode().toString());
        dto.setLocation(job.getLocation());
        dto.setJobDescription(job.getDescription());

        Employer employer = job.getEmployer();
        dto.setCompanyName(employer.getCompanyName());
        dto.setCompanyLogoUrl(employer.getProfilePictureUrl());

        dto.setResumeUrl(application.getResumeUrl());

        return dto;
    }

    private ApplicationViewForEmployerDTO mapToEmployerDTO(Application app) {
        JobSeeker applicant = app.getJobSeeker();
        Job job = app.getJob();

        ApplicationViewForEmployerDTO dto = new ApplicationViewForEmployerDTO();
        dto.setApplicantUsername(applicant.getUsername());
        dto.setApplicantDOB(applicant.getDob());
        dto.setApplicantEmail(applicant.getEmail());
        dto.setId(app.getId());
        dto.setResumeUrl(app.getResumeUrl());
        dto.setApplicantName(applicant.getName());
        dto.setApplicantProfilePicture(applicant.getProfilePictureUrl());
        dto.setStatus(app.getStatus());
        dto.setAppliedAt(app.getAppliedAt());

        dto.setJobTitle(job.getTitle());

        return dto;
    }

    private void notifyObservers(JobSeeker jobSeeker, Application application) {
        for (ApplicationObserver observer : observers) {
            observer.notify(jobSeeker, application);
        }
    }
}
