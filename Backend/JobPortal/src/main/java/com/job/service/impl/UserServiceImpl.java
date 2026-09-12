package com.job.service.impl;

import com.job.dto.request.EmployerRegisterRequestDTO;
import com.job.dto.request.JobSeekerRegisterRequestDTO;
import com.job.dto.response.AuthResponseDTO;
import com.job.entity.Employer;
import com.job.entity.JobSeeker;
import com.job.entity.User;
import com.job.enums.Role;
import com.job.exception.DuplicateResourceException;
import com.job.exception.ResourceNotFoundException;
import com.job.repository.EmployerRepository;
import com.job.repository.JobSeekerRepository;
import com.job.repository.UserRepository;
import com.job.service.interfaces.IUserService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public JobSeeker registerJobSeekerWithoutFiles(JobSeekerRegisterRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        log.info("Registering new job seeker: {}", dto.getUsername());
        JobSeeker jobSeeker = new JobSeeker();
        jobSeeker.setName(dto.getName());
        jobSeeker.setUsername(dto.getUsername());
        jobSeeker.setPassword(passwordEncoder.encode(dto.getPassword()));
        jobSeeker.setDob(dto.getDob());
        jobSeeker.setRole(Role.JOB_SEEKER);
        jobSeeker.setEmail(dto.getEmail());
        jobSeeker.setResumeUrl(null);
        jobSeeker.setProfilePictureUrl(null);
        return jobSeekerRepository.save(jobSeeker);
    }

    @Override
    @Transactional
    public Employer registerEmployer(EmployerRegisterRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        log.info("Registering new employer: {}", dto.getUsername());
        Employer employer = new Employer();
        employer.setName(dto.getName());
        employer.setUsername(dto.getUsername());
        employer.setPassword(passwordEncoder.encode(dto.getPassword()));
        employer.setCompanyName(dto.getCompanyName());
        employer.setProfilePictureUrl(null);
        employer.setRole(Role.EMPLOYER);
        employer.setEmail(dto.getEmail());
        employer.setIndustry(dto.getIndustry());
        return employerRepository.save(employer);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(@NotBlank(message = "Username is required") String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO buildAuthResponse(User user, String token) {
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setRole(user.getRole().name());
        return response;
    }

}
