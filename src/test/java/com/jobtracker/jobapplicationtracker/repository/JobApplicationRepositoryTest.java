package com.jobtracker.jobapplicationtracker.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.jobtracker.jobapplicationtracker.entity.JobApplication;
import com.jobtracker.jobapplicationtracker.entity.User;

@DataJpaTest
public class JobApplicationRepositoryTest {

	@Autowired
	private JobApplicationRepository jobApplicationRepository;

	@Autowired
	private UserRepository userRepository;

	private User testUser;
	private JobApplication jobApplication1;
	private JobApplication jobApplication2;

	@BeforeEach
	public void setUp() {
		// Create and save a user for testing using the User constructor
		testUser = new User(null, "testUser", "testuser@example.com", "password123");
		userRepository.save(testUser);

		// Create and save job applications for the user using the JobApplication
		// constructor
		jobApplication1 = new JobApplication(null, "Company A", "Developer", LocalDate.now(),
				JobApplication.ApplicationStatus.PENDING, "Notes for Company A", testUser);
		jobApplication2 = new JobApplication(null, "Company B", "Tester", LocalDate.now(),
				JobApplication.ApplicationStatus.INTERVIEWING, "Notes for Company B", testUser);

		jobApplicationRepository.save(jobApplication1);
		jobApplicationRepository.save(jobApplication2);
	}

	@Test
	public void testFindByUserId() {
		List<JobApplication> jobApplications = jobApplicationRepository.findByUserId(testUser.getId());

		// Verify that both job applications are returned for the test user
		assertEquals(2, jobApplications.size());
		assertEquals(jobApplication1.getCompany(), jobApplications.get(0).getCompany());
		assertEquals(jobApplication2.getCompany(), jobApplications.get(1).getCompany());
	}

	@Test
	public void testFilterJobApplications() {
		// Filter by company name "Company A"
		List<JobApplication> filteredJobApplications = jobApplicationRepository.filterJobApplications("Company A", null,
				null);

		// Verify that only jobApplication1 is returned
		assertEquals(1, filteredJobApplications.size());
		assertEquals("Company A", filteredJobApplications.get(0).getCompany());

		// Filter by role "Developer" and status "APPLIED"
		filteredJobApplications = jobApplicationRepository.filterJobApplications(null, "Developer",
				JobApplication.ApplicationStatus.PENDING);

		// Verify that only jobApplication1 is returned
		assertEquals(1, filteredJobApplications.size());
		assertEquals("Developer", filteredJobApplications.get(0).getRole());
		assertEquals(JobApplication.ApplicationStatus.PENDING, filteredJobApplications.get(0).getStatus());
	}

	@Test
	public void testFilterJobApplicationsWithNullValues() {
		// Test filtering with all null values
		List<JobApplication> filteredJobApplications = jobApplicationRepository.filterJobApplications(null, null, null);

		// Verify that both job applications are returned
		assertEquals(2, filteredJobApplications.size());
	}
}
