package com.job.service.interfaces;

import com.job.dto.response.JobResponseDTO;
import com.job.entity.JobSeeker;

import java.util.List;

public interface ISavedJobService {
    void saveJob(JobSeeker jobSeeker, Long jobId);
    void unsaveJob(JobSeeker jobSeeker, Long jobId);
    List<JobResponseDTO> getSavedJobs(JobSeeker jobSeeker);
}
