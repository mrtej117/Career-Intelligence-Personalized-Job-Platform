package com.job.entity;

import com.job.enums.JobType;
import com.job.enums.WorkMode;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String description;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    private JobType type;

    @Enumerated(EnumType.STRING)
    private WorkMode workMode;

    private LocalDateTime postedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employer employer;

    @ElementCollection(fetch = FetchType.LAZY)
    @BatchSize(size = 25)
    private List<String> responsibilities;

    @ElementCollection(fetch = FetchType.LAZY)
    @BatchSize(size = 25)
    private List<String> requiredSkills;

    @ElementCollection(fetch = FetchType.LAZY)
    @BatchSize(size = 25)
    private List<String> screeningQuestions;


    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications;
}
