package com.jobtracker.jobapplicationtracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jobtracker.jobapplicationtracker.entity.JobApplication;
import com.jobtracker.jobapplicationtracker.repository.JobApplicationRepository;

public class JobApplicationServiceImplTest {

	@Mock
	private JobApplicationRepository jobApplicationRepository;

	@InjectMocks
	private JobApplicationServiceImpl jobApplicationService;

	private JobApplication jobApplication;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this); // Initialize mocks
		jobApplication = new JobApplication();
		jobApplication.setId(1L);
		jobApplication.setCompany("Company A");
		jobApplication.setRole("Developer");
		jobApplication.setApplicationDate(LocalDate.of(2025, 1, 1));
		jobApplication.setStatus(JobApplication.ApplicationStatus.PENDING);
		jobApplication.setNotes("Notes");
	}

	@Test
	public void testGetAllApplicationsByUser() {
		Long userId = 1L;
		when(jobApplicationRepository.findByUserId(userId)).thenReturn(Arrays.asList(jobApplication));

		List<JobApplication> applications = jobApplicationService.getAllApplicationsByUser(userId);

		assertNotNull(applications);
		assertEquals(1, applications.size());
		assertEquals("Company A", applications.get(0).getCompany());
	}

	@Test
	public void testSaveApplication() {
		when(jobApplicationRepository.save(jobApplication)).thenReturn(jobApplication);

		JobApplication savedApplication = jobApplicationService.saveApplication(jobApplication);

		assertNotNull(savedApplication);
		assertEquals("Company A", savedApplication.getCompany());
		verify(jobApplicationRepository, times(1)).save(jobApplication);
	}

	@Test
	public void testDeleteApplication() {
		Long applicationId = 1L;
		doNothing().when(jobApplicationRepository).deleteById(applicationId);

		jobApplicationService.deleteApplication(applicationId);

		verify(jobApplicationRepository, times(1)).deleteById(applicationId);
	}

	@Test
	public void testUpdateApplication() {
		Long applicationId = 1L;
		JobApplication updatedApplication = new JobApplication();
		updatedApplication.setId(1L);
		updatedApplication.setCompany("Company B");
		updatedApplication.setRole("Tester");
		updatedApplication.setNotes("Updated Notes");

		when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(jobApplication));
		when(jobApplicationRepository.save(any(JobApplication.class))).thenReturn(updatedApplication);

		JobApplication result = jobApplicationService.updateApplication(applicationId, updatedApplication);

		assertNotNull(result);
		assertEquals("Company B", result.getCompany());
		assertEquals("Tester", result.getRole());
		assertEquals("Updated Notes", result.getNotes());
		verify(jobApplicationRepository, times(1)).findById(applicationId);
		verify(jobApplicationRepository, times(1)).save(any(JobApplication.class));
	}

	@Test
	public void testFilterJobApplications() {
		String company = "Company A";
		String role = "Developer";
		String status = "PENDING";

		when(jobApplicationRepository.filterJobApplications(company, role, JobApplication.ApplicationStatus.PENDING))
				.thenReturn(Arrays.asList(jobApplication));

		List<JobApplication> applications = jobApplicationService.filterJobApplications(company, role, status);

		assertNotNull(applications);
		assertEquals(1, applications.size());
		assertEquals("Company A", applications.get(0).getCompany());
		assertEquals(JobApplication.ApplicationStatus.PENDING, applications.get(0).getStatus());
	}
}
