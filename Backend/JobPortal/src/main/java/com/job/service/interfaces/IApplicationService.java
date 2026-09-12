package com.job.service.interfaces;

import com.job.dto.request.ApplicationRequestDTO;
import com.job.dto.response.ApplicationResponseDTO;
import com.job.dto.response.ApplicationViewForEmployerDTO;
import com.job.dto.response.PageResponseDTO;
import com.job.entity.Employer;
import com.job.entity.JobSeeker;
import com.job.enums.ApplicationStatus;

import java.util.List;

public interface IApplicationService {
    ApplicationResponseDTO applyToJob(ApplicationRequestDTO dto, JobSeeker jobSeeker);
    List<ApplicationViewForEmployerDTO> getAllApplicationsForEmployer(Employer employer, ApplicationStatus status);
    PageResponseDTO<ApplicationResponseDTO> getMyApplications(JobSeeker jobSeeker, int page, int size);
    ApplicationResponseDTO getApplicationById(Long id, JobSeeker requester);
    void withdrawApplication(Long id, JobSeeker requester);
    List<ApplicationViewForEmployerDTO> getApplicationsForJob(Long jobId, Employer employer);
    ApplicationViewForEmployerDTO getApplicationViewForEmployer(Long id, Employer employer);
    void updateApplicationStatus(Long applicationId, ApplicationStatus newStatus, Employer employer);
    boolean hasUserAppliedToJob(Long jobId, JobSeeker jobSeeker);
}