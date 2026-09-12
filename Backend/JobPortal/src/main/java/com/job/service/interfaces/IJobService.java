package com.job.service.interfaces;

import com.job.dto.request.JobRequestDTO;
import com.job.dto.response.JobResponseDTO;
import com.job.dto.response.PageResponseDTO;
import com.job.entity.Employer;
import com.job.entity.Job;

import java.util.List;

public interface IJobService {
    Job createJob(JobRequestDTO dto, Employer employer);
    PageResponseDTO<JobResponseDTO> getAllJobsSortedByDate(int page, int size);
    PageResponseDTO<JobResponseDTO> searchByTitle(String keyword, int page, int size);
    PageResponseDTO<JobResponseDTO> searchByType(String type, int page, int size);
    PageResponseDTO<JobResponseDTO> searchByLocation(String location, int page, int size);
    PageResponseDTO<JobResponseDTO> searchByWorkMode(String workMode, int page, int size);
    JobResponseDTO getJobById(Long id);
    JobResponseDTO updateJob(Long id, JobRequestDTO dto, Employer employer);
    void deleteJob(Long jobId, Employer employer);
    List<JobResponseDTO> getJobsByEmployer(Employer employer);
}
