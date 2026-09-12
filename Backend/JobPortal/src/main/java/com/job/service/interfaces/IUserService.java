package com.job.service.interfaces;

import com.job.dto.request.EmployerRegisterRequestDTO;
import com.job.dto.request.JobSeekerRegisterRequestDTO;
import com.job.dto.response.AuthResponseDTO;
import com.job.entity.Employer;
import com.job.entity.JobSeeker;
import com.job.entity.User;

public interface IUserService {
    User findByUsername(String username);
    User getUserByUsername(String username);
    boolean usernameExists(String username);
    JobSeeker registerJobSeekerWithoutFiles(JobSeekerRegisterRequestDTO dto);
    Employer registerEmployer(EmployerRegisterRequestDTO dto);
    AuthResponseDTO buildAuthResponse(User user, String token);
}