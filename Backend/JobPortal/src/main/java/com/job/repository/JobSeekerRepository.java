package com.job.repository;

import com.job.entity.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSeekerRepository extends JpaRepository<JobSeeker, Long> {
    // Later: Optional<JobSeeker> findByUserId(Long userId);
}
