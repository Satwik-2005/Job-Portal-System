package com.jobportal.repository;

import com.jobportal.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    Optional<JobApplication> findByJobIdAndJobSeekerId(Long jobId, Long jobSeekerId);
    List<JobApplication> findByJobSeekerId(Long jobSeekerId);
    List<JobApplication> findByJobId(Long jobId);
}
