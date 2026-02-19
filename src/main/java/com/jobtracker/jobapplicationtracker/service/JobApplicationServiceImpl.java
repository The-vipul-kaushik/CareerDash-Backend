package com.jobtracker.jobapplicationtracker.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobtracker.jobapplicationtracker.dto.JobApplicationDTO;
import com.jobtracker.jobapplicationtracker.dto.JobApplicationMapper;
import com.jobtracker.jobapplicationtracker.entity.JobApplication;
import com.jobtracker.jobapplicationtracker.entity.User;
import com.jobtracker.jobapplicationtracker.repository.JobApplicationRepository;
import com.jobtracker.jobapplicationtracker.repository.UserRepository;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(JobApplicationServiceImpl.class);

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    @Autowired
    public JobApplicationServiceImpl(JobApplicationRepository jobApplicationRepository,
                                     UserRepository userRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<JobApplicationDTO> getApplicationsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return jobApplicationRepository.findByUserId(user.getId())
                .stream()
                .map(JobApplicationMapper::toDTO)
                .toList();
    }

    @Override
    public JobApplicationDTO getApplicationForUser(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        JobApplication app = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with ID: " + id));

        if (!app.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Forbidden: You cannot access this application");
        }
        return JobApplicationMapper.toDTO(app);
    }

    @Override
    public JobApplicationDTO createApplication(JobApplication jobApplication, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        jobApplication.setUser(user);

        if (jobApplication.getStatus() == null) {
            jobApplication.setStatus(JobApplication.ApplicationStatus.APPLIED);
        }

        JobApplication saved = jobApplicationRepository.save(jobApplication);
        return JobApplicationMapper.toDTO(saved);
    }

    @Override
    public JobApplicationDTO updateApplication(Long id, JobApplication updatedApplication, String username) {
        JobApplication existingApp = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (!existingApp.getUser().getUsername().equals(username)) {
            throw new SecurityException("Forbidden: You cannot update this application");
        }

        existingApp.setCompany(updatedApplication.getCompany());
        existingApp.setRole(updatedApplication.getRole());
        existingApp.setApplicationDate(updatedApplication.getApplicationDate());
        existingApp.setStatus(updatedApplication.getStatus());
        existingApp.setNotes(updatedApplication.getNotes());

        JobApplication updated = jobApplicationRepository.save(existingApp);
        return JobApplicationMapper.toDTO(updated);
    }

    @Override
    public void deleteApplication(Long id, String username) {
        JobApplication existingApp = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (!existingApp.getUser().getUsername().equals(username)) {
            throw new SecurityException("Forbidden: You cannot delete this application");
        }

        jobApplicationRepository.delete(existingApp);
    }

    @Override
    public List<JobApplicationDTO> filterJobApplications(String company, String role, String status, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        JobApplication.ApplicationStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = JobApplication.ApplicationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + status);
            }
        }

        return jobApplicationRepository.filterJobApplications(
                user.getId(),
                (company != null && !company.trim().isEmpty()) ? company : null,
                (role != null && !role.trim().isEmpty()) ? role : null,
                statusEnum
        ).stream()
         .map(JobApplicationMapper::toDTO)
         .toList();
    }
}