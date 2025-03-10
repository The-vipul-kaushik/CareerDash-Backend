package com.jobtracker.jobapplicationtracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobtracker.jobapplicationtracker.entity.JobApplication;
import com.jobtracker.jobapplicationtracker.entity.User;
import com.jobtracker.jobapplicationtracker.service.JobApplicationService;
import com.jobtracker.jobapplicationtracker.service.UserService;

@CrossOrigin(origins = "${cors.allowed-origin}")
//@CrossOrigin("*")
@RestController
@RequestMapping("/api/job-applications")
public class JobApplicationController {

	@Autowired
	private JobApplicationService jobApplicationService;

	@Autowired
	private UserService userService;

	// Get all job applications for the logged-in user
	@GetMapping
	public ResponseEntity<List<JobApplication>> getAllJobApplicationsForUser(
			@RequestParam(required = false) String company, @RequestParam(required = false) String role,
			@RequestParam(required = false) String status) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName(); // Get the logged-in user's username
		Long userId = userService.getUserByUsername(username).getId(); // Get user ID from the username

		// If no filter parameters are provided, get all applications
		if (company == null && role == null && status == null) {
			return ResponseEntity.ok(jobApplicationService.getAllApplicationsByUser(userId));
		}

		// Otherwise, filter the applications
		List<JobApplication> filteredApplications = jobApplicationService.filterJobApplications(company, role, status);
		return ResponseEntity.ok(filteredApplications);
	}

	// Get a specific job application by ID
	@GetMapping("/{id}")
	public ResponseEntity<JobApplication> getJobApplicationById(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName(); // Get the logged-in user's username
		Long userId = userService.getUserByUsername(username).getId(); // Get user ID from the username

		JobApplication jobApplication = jobApplicationService.getApplicationById(id);

		// Ensure the logged-in user owns this application
		if (!jobApplication.getUser().getId().equals(userId)) {
			return ResponseEntity.status(403).build(); // Forbidden if user doesn't own the application
		}

		return ResponseEntity.ok(jobApplication);
	}

	// Save a new job application
	@PostMapping
	public ResponseEntity<JobApplication> createJobApplication(@RequestBody JobApplication jobApplication) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName(); // Get the logged-in user's username
		User user = userService.getUserByUsername(username); // Get user by username
		jobApplication.setUser(user); // Automatically set the logged-in user to the job application
		JobApplication savedApplication = jobApplicationService.saveApplication(jobApplication);
		return ResponseEntity.ok(savedApplication);
	}

	// Update a job application
	@PutMapping("/{id}")
	public ResponseEntity<JobApplication> updateJobApplication(@PathVariable Long id,
			@RequestBody JobApplication jobApplication) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName(); // Get the logged-in user's username
		Long userId = userService.getUserByUsername(username).getId(); // Get user ID from the username
		JobApplication existingApplication = jobApplicationService.getAllApplicationsByUser(userId).stream()
				.filter(application -> application.getId().equals(id)).findFirst()
				.orElseThrow(() -> new RuntimeException("Application not found"));

		jobApplication.setUser(existingApplication.getUser()); // Ensure the user is the same
		jobApplication.setId(existingApplication.getId()); // Set the ID to the existing one
		JobApplication updatedApplication = jobApplicationService.updateApplication(id, jobApplication);
		return ResponseEntity.ok(updatedApplication);
	}

	// Delete a job application
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteJobApplication(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName(); // Get the logged-in user's username
		Long userId = userService.getUserByUsername(username).getId(); // Get user ID from the username
		JobApplication existingApplication = jobApplicationService.getAllApplicationsByUser(userId).stream()
				.filter(application -> application.getId().equals(id)).findFirst()
				.orElseThrow(() -> new RuntimeException("Application not found"));

		jobApplicationService.deleteApplication(id);
		return ResponseEntity.ok("Application deleted successfully");
	}

}
