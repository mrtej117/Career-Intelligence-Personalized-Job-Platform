package com.job.repository;

import com.job.entity.Application;
import com.job.entity.Employer;
import com.job.entity.Job;
import com.job.entity.JobSeeker;
import com.job.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query(value = "SELECT a FROM Application a JOIN FETCH a.job j JOIN FETCH j.employer WHERE a.jobSeeker = :jobSeeker",
           countQuery = "SELECT COUNT(a) FROM Application a WHERE a.jobSeeker = :jobSeeker")
    Page<Application> findByJobSeeker(@Param("jobSeeker") JobSeeker jobSeeker, Pageable pageable);

    boolean existsByJobAndJobSeeker(Job job, JobSeeker jobSeeker);
    boolean existsByJobIdAndJobSeeker(Long jobId, JobSeeker jobSeeker);
    @Query("SELECT a FROM Application a JOIN FETCH a.jobSeeker JOIN FETCH a.job j JOIN FETCH j.employer WHERE a.job = :job")
    List<Application> findByJob(@Param("job") Job job);

    @Query("SELECT a FROM Application a JOIN FETCH a.jobSeeker JOIN FETCH a.job j JOIN FETCH j.employer WHERE j.employer = :employer")
    List<Application> findByJob_Employer(@Param("employer") Employer employer);
    List<Application> findByJob_EmployerAndStatus(Employer employer, ApplicationStatus status);
}
