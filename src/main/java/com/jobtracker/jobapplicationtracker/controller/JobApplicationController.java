package com.jobtracker.jobapplicationtracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobapplicationtracker.dto.ApiResponse;
import com.jobtracker.jobapplicationtracker.dto.JobApplicationDTO;
import com.jobtracker.jobapplicationtracker.entity.JobApplication;
import com.jobtracker.jobapplicationtracker.service.JobApplicationService;

@CrossOrigin(origins = "${cors.allowed-origin}")
@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    @Autowired
    private JobApplicationService jobApplicationService;

    // Get all applications for logged-in user
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobApplicationDTO>>> getApplications(Authentication authentication) {
        String username = authentication.getName();
        List<JobApplicationDTO> apps = jobApplicationService.getApplicationsForUser(username);
        ApiResponse<List<JobApplicationDTO>> response =
                new ApiResponse<>(apps, "Applications fetched successfully", 200);
        return ResponseEntity.ok(response);
    }

    // Get single application by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobApplicationDTO>> getApplication(@PathVariable Long id,
                                                                         Authentication authentication) {
        String username = authentication.getName();
        JobApplicationDTO app = jobApplicationService.getApplicationForUser(id, username);
        ApiResponse<JobApplicationDTO> response =
                new ApiResponse<>(app, "Application fetched successfully", 200);
        return ResponseEntity.ok(response);
    }

    // Create new application
    @PostMapping
    public ResponseEntity<ApiResponse<JobApplicationDTO>> createApplication(@RequestBody JobApplication jobApplication,
                                                                            Authentication authentication) {
        String username = authentication.getName();
        JobApplicationDTO saved = jobApplicationService.createApplication(jobApplication, username);
        ApiResponse<JobApplicationDTO> response =
                new ApiResponse<>(saved, "Application created successfully", 201);
        return ResponseEntity.status(201).body(response);
    }

    // Update application
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobApplicationDTO>> updateApplication(@PathVariable Long id,
                                                                            @RequestBody JobApplication jobApplication,
                                                                            Authentication authentication) {
        String username = authentication.getName();
        JobApplicationDTO updated = jobApplicationService.updateApplication(id, jobApplication, username);
        ApiResponse<JobApplicationDTO> response =
                new ApiResponse<>(updated, "Application updated successfully", 200);
        return ResponseEntity.ok(response);
    }

    // Delete application
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(@PathVariable Long id,
                                                               Authentication authentication) {
        String username = authentication.getName();
        jobApplicationService.deleteApplication(id, username);
        ApiResponse<Void> response =
                new ApiResponse<>(null, "Application deleted successfully", 204);
        return ResponseEntity.status(204).body(response);
    }
}