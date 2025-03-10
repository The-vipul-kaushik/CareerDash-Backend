package com.jobtracker.jobapplicationtracker.service;

import java.util.List;

import com.jobtracker.jobapplicationtracker.entity.JobApplication;

public interface JobApplicationService {
	List<JobApplication> getAllApplicationsByUser(Long userId);

	JobApplication saveApplication(JobApplication jobApplication);

	void deleteApplication(Long id);

	JobApplication updateApplication(Long id, JobApplication updatedApplication);

	List<JobApplication> filterJobApplications(String company, String role, String status);

	JobApplication getApplicationById(Long id);

}
