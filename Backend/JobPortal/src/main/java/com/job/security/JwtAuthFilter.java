package com.job.security;

import com.job.entity.Employer;
import com.job.entity.JobSeeker;
import com.job.entity.User;
import com.job.enums.Role;
import com.job.repository.EmployerRepository;
import com.job.repository.JobSeekerRepository;
import com.job.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final JobSeekerRepository jobSeekerRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No token found in Authorization header for request: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("JWT token received for request: {}", request.getRequestURI());
        String token = authHeader.substring(7);

        String username;
        try {
            username = jwtUtil.extractUsername(token);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT for request: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\":\"TOKEN_EXPIRED\",\"message\":\"Your session has expired, please log in again\"}"
            );
            return;
        }

        if (username == null) {
            log.error("Could not extract username from token");
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Username extracted from token: {}", username);

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            log.warn("No user found with username: {}", username);
            filterChain.doFilter(request, response);
            return;
        }

        User user = optionalUser.get();

        String roleName = "ROLE_" + user.getRole().name();
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(roleName));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        log.debug("Security context set for user: {} with role: {}", username, roleName);

        if (user.getRole() == Role.EMPLOYER) {
            employerRepository.findById(user.getId()).ifPresentOrElse(
                    employer -> request.setAttribute("user", employer),
                    () -> log.warn("Employer record not found for user id: {}", user.getId())
            );
        } else if (user.getRole() == Role.JOB_SEEKER) {
            jobSeekerRepository.findById(user.getId()).ifPresentOrElse(
                    jobSeeker -> request.setAttribute("user", jobSeeker),
                    () -> log.warn("JobSeeker record not found for user id: {}", user.getId())
            );
        } else {
            request.setAttribute("user", user);
        }

        filterChain.doFilter(request, response);
    }
}
