package com.jobtracker.jobapplicationtracker.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobtracker.jobapplicationtracker.entity.JobApplication;
import com.jobtracker.jobapplicationtracker.repository.JobApplicationRepository;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

	private static final Logger logger = LoggerFactory.getLogger(JobApplicationServiceImpl.class);

	private final JobApplicationRepository jobApplicationRepository;

	@Autowired
	public JobApplicationServiceImpl(JobApplicationRepository jobApplicationRepository) {
		this.jobApplicationRepository = jobApplicationRepository;
	}

	@Override
	public List<JobApplication> getAllApplicationsByUser(Long userId) {
		logger.info("Fetching all job applications for user with ID: {}", userId);
		List<JobApplication> applications = jobApplicationRepository.findByUserId(userId);
		logger.info("Found {} applications for user with ID: {}", applications.size(), userId);
		return applications;
	}

	@Override
	public JobApplication getApplicationById(Long id) {
		logger.info("Fetching job application with ID: {}", id);
		return jobApplicationRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Application not found with ID: " + id));
	}

	@Override
	public JobApplication saveApplication(JobApplication jobApplication) {
		logger.info("Saving job application for company: {}", jobApplication.getCompany());
		logger.info(">>>", jobApplication.getStatus());
		JobApplication savedApplication = jobApplicationRepository.save(jobApplication);
		logger.info("Job application saved successfully with ID: {}", savedApplication.getId());
		return savedApplication;
	}

	@Override
	public void deleteApplication(Long id) {
		logger.info("Attempting to delete job application with ID: {}", id);
		jobApplicationRepository.deleteById(id);
		logger.info("Job application with ID: {} deleted successfully", id);
	}

	@Override
	public JobApplication updateApplication(Long id, JobApplication updatedApplication) {
		logger.info("Updating job application with ID: {}", id);
		JobApplication existingApplication = jobApplicationRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Application not found"));

		existingApplication.setCompany(updatedApplication.getCompany());
		existingApplication.setRole(updatedApplication.getRole());
		existingApplication.setApplicationDate(updatedApplication.getApplicationDate());
		existingApplication.setStatus(updatedApplication.getStatus());
		existingApplication.setNotes(updatedApplication.getNotes());

		JobApplication updated = jobApplicationRepository.save(existingApplication);
		logger.info("Job application with ID: {} updated successfully", updated.getId());
		return updated;
	}

	@Override
	public List<JobApplication> filterJobApplications(String company, String role, String status) {
		JobApplication.ApplicationStatus statusEnum = null;
		if (status != null && !status.isEmpty()) {
			try {
				statusEnum = JobApplication.ApplicationStatus.valueOf(status.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Invalid status value: " + status);
			}
		}

		return jobApplicationRepository.filterJobApplications(
				(company != null && !company.trim().isEmpty()) ? company : null,
				(role != null && !role.trim().isEmpty()) ? role : null, statusEnum);
	}
}
