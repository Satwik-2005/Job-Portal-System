package com.jobportal.repository;

import com.jobportal.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    Page<Job> findAll(Pageable pageable);
    List<Job> findByTitleContainingIgnoreCase(String title);
    // Retrieve jobs owned by a specific recruiter (used for recruiter dashboard)
    @Query("SELECT j FROM Job j WHERE j.recruiter.id = :recruiterId")
    Page<Job> findAllByRecruiterId(@Param("recruiterId") Long recruiterId, Pageable pageable);
    // Additional query methods for filtering can be added as needed
}
