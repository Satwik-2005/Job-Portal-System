package com.jobportal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    private String requirements;
    private String responsibilities;
    private String location;
    private String jobType; // e.g., FULL_TIME, PART_TIME, CONTRACT
    private String experienceLevel; // e.g., JUNIOR, MID, SENIOR
    private Double minimumSalary;
    private Double maximumSalary;
    private String skillsRequired; // comma‑separated or JSON
    private String status; // OPEN, CLOSED, DELETED

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private Instant applicationDeadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter; // the recruiter user who created the job

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> applications;
}
