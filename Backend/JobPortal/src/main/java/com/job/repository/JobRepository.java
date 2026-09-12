package com.job.repository;

import com.job.entity.Employer;
import com.job.entity.Job;
import com.job.enums.JobType;
import com.job.enums.WorkMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByEmployer(Employer employer);

    @Query(value = "SELECT j FROM Job j JOIN FETCH j.employer",
            countQuery = "SELECT COUNT(j) FROM Job j")
    Page<Job> findAllWithEmployer(Pageable pageable);

    @Query(value = "SELECT j FROM Job j JOIN FETCH j.employer WHERE j.id = :id")
    Optional<Job> findByIdWithEmployer(@Param("id") Long id);

    @Query("SELECT j FROM Job j JOIN FETCH j.employer e WHERE LOWER(j.title) = LOWER(:title) AND LOWER(e.companyName) = LOWER(:companyName)")
    List<Job> findFirstByTitleAndCompanyName(@Param("title") String title, @Param("companyName") String companyName);

    @Query(value = "SELECT j FROM Job j JOIN FETCH j.employer WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))",
            countQuery = "SELECT COUNT(j) FROM Job j WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Job> findByTitleContainingIgnoreCaseWithEmployer(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT j FROM Job j JOIN FETCH j.employer WHERE j.type = :type",
            countQuery = "SELECT COUNT(j) FROM Job j WHERE j.type = :type")
    Page<Job> findByTypeWithEmployer(@Param("type") JobType type, Pageable pageable);

    @Query(value = "SELECT j FROM Job j JOIN FETCH j.employer WHERE LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))",
            countQuery = "SELECT COUNT(j) FROM Job j WHERE LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    Page<Job> findByLocationContainingIgnoreCaseWithEmployer(@Param("location") String location, Pageable pageable);

    @Query(value = "SELECT j FROM Job j JOIN FETCH j.employer WHERE j.workMode = :workMode",
            countQuery = "SELECT COUNT(j) FROM Job j WHERE j.workMode = :workMode")
    Page<Job> findByWorkModeWithEmployer(@Param("workMode") WorkMode workMode, Pageable pageable);

    @Query("SELECT j FROM Job j JOIN FETCH j.employer WHERE j.employer = :employer")
    List<Job> findByEmployerWithEmployer(@Param("employer") Employer employer);
}