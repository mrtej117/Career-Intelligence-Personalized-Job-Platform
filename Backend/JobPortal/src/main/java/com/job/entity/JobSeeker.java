package com.job.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobSeeker extends User {

    @Column(name = "resume_url")
    private String resumeUrl;
    private String resumeOriginalName;
    @Column(nullable = false)
    private LocalDate dob;

    @OneToMany(mappedBy = "jobSeeker", fetch = FetchType.LAZY)
    private List<Application> applications;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "saved_jobs",
            joinColumns = @JoinColumn(name = "job_seeker_id"),
            inverseJoinColumns = @JoinColumn(name = "job_id")
    )
    private Set<Job> savedJobs = new HashSet<>();

}
