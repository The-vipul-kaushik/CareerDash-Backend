package com.jobtracker.jobapplicationtracker.service;

import com.jobtracker.jobapplicationtracker.dto.JobApplicationDTO;
import com.jobtracker.jobapplicationtracker.entity.JobApplication;

import java.util.List;

public interface JobApplicationService {

    // Get all applications for a user
    List<JobApplicationDTO> getApplicationsForUser(String username);

    // Get a single application for a user
    JobApplicationDTO getApplicationForUser(Long id, String username);

    // Create new application
    JobApplicationDTO createApplication(JobApplication jobApplication, String username);

    // Update application
    JobApplicationDTO updateApplication(Long id, JobApplication updatedApplication, String username);

    // Delete application
    void deleteApplication(Long id, String username);

    // Filter applications (scoped to user)
    List<JobApplicationDTO> filterJobApplications(String company, String role, String status, String username);
}