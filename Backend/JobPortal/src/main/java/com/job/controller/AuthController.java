package com.job.controller;

import com.job.exception.ResourceNotFoundException;
import com.job.dto.request.EmployerRegisterRequestDTO;
import com.job.dto.request.JobSeekerRegisterRequestDTO;
import com.job.dto.request.LoginRequestDTO;
import com.job.dto.response.AuthResponseDTO;
import com.job.entity.User;
import com.job.security.JwtUtil;
import com.job.service.interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup/jobseeker")
    public ResponseEntity<String> signUpJobSeeker(@RequestBody @Valid JobSeekerRegisterRequestDTO dto) {
        log.info("Job seeker signup attempt for username: {}", dto.getUsername());
        userService.registerJobSeekerWithoutFiles(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Job seeker signed up successfully!");
    }

    @PostMapping("/signup/employer")
    public ResponseEntity<String> signUpEmployer(@RequestBody @Valid EmployerRegisterRequestDTO dto) {
        log.info("Employer signup attempt for username: {}", dto.getUsername());
        userService.registerEmployer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Employer signed up successfully!");
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        log.info("Sign-in attempt for username: {}", dto.getUsername());
        User user;
        try {
            user = userService.getUserByUsername(dto.getUsername());
        } catch (ResourceNotFoundException e) {
            log.warn("Failed sign-in attempt for username: {}", dto.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            log.warn("Failed sign-in attempt for username: {}", dto.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.generateToken(user.getUsername());
        log.info("Sign-in successful for username: {}", dto.getUsername());

        return ResponseEntity.ok(userService.buildAuthResponse(user, token));
    }
}
