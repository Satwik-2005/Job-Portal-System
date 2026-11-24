package com.jobportal.service;

import com.jobportal.dto.JobCreateRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.entity.Company;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.RecruiterProfileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;

    @Autowired
    private com.jobportal.repository.UserRepository userRepository;

    private ModelMapper mapper = new ModelMapper();

    /**
     * Create a new job. The recruiterId is obtained from the authenticated user.
     */
    @Transactional
    public JobResponse createJob(JobCreateRequest request, Long recruiterId) {
        // Verify recruiter owns the company
        var recruiterProfile = recruiterProfileRepository.findByUserId(recruiterId)
                .orElseThrow(() -> new IllegalArgumentException("Recruiter profile not found"));
        Company company = recruiterProfile.getCompany();
        if (!company.getId().equals(request.getCompanyId())) {
            throw new IllegalArgumentException("Company does not belong to recruiter");
        }
        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .responsibilities(request.getResponsibilities())
                .location(request.getLocation())
                .jobType(request.getJobType())
                .experienceLevel(request.getExperienceLevel())
                .minimumSalary(request.getMinimumSalary())
                .maximumSalary(request.getMaximumSalary())
                .skillsRequired(request.getSkillsRequired())
                .status(request.getStatus())
                .applicationDeadline(request.getApplicationDeadline())
                .company(company)
                .recruiter(userRepository.findById(recruiterId).orElseThrow(() -> new IllegalArgumentException("Recruiter user not found")))
                .build();
        Job saved = jobRepository.save(job);
        return mapper.map(saved, JobResponse.class);
    }

    @Transactional
    public JobResponse updateJob(Long jobId, JobCreateRequest request, Long recruiterId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        // Ownership check
        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new IllegalArgumentException("You are not the owner of this job");
        }
        // Update mutable fields
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setResponsibilities(request.getResponsibilities());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setMinimumSalary(request.getMinimumSalary());
        job.setMaximumSalary(request.getMaximumSalary());
        job.setSkillsRequired(request.getSkillsRequired());
        job.setStatus(request.getStatus());
        job.setApplicationDeadline(request.getApplicationDeadline());
        // company change not allowed here – keep original
        Job saved = jobRepository.save(job);
        return mapper.map(saved, JobResponse.class);
    }

    @Transactional
    public void deleteJob(Long jobId, Long recruiterId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new IllegalArgumentException("You are not the owner of this job");
        }
        jobRepository.delete(job);
    }

    public JobResponse getJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        return mapper.map(job, JobResponse.class);
    }

    public Page<JobResponse> listPublicJobs(Pageable pageable) {
        Page<Job> page = jobRepository.findAll(pageable);
        return page.map(job -> mapper.map(job, JobResponse.class));
    }

    public Page<JobResponse> listRecruiterJobs(Long recruiterId, Pageable pageable) {
        Page<Job> page = jobRepository.findAllByRecruiterId(recruiterId, pageable);
        return page.map(job -> mapper.map(job, JobResponse.class));
    }
}
