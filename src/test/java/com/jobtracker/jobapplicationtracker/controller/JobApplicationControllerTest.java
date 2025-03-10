package com.jobtracker.jobapplicationtracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.jobapplicationtracker.entity.JobApplication;
import com.jobtracker.jobapplicationtracker.entity.User;
import com.jobtracker.jobapplicationtracker.service.JobApplicationService;
import com.jobtracker.jobapplicationtracker.service.UserService;
import com.jobtracker.jobapplicationtracker.utils.JwtUtil;

@WebMvcTest(controllers = JobApplicationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class JobApplicationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private JobApplicationService jobApplicationService;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private JwtUtil jwtUtil;

	private User mockUser;
	private JobApplication mockJobApplication;

	@BeforeEach
	void setUp() {
		mockUser = User.builder().id(1L).username("testuser").email("test@example.com").password("password").build();
		mockJobApplication = new JobApplication(1L, "Company A", "Software Engineer", LocalDate.now(),
				JobApplication.ApplicationStatus.PENDING, "Notes", mockUser);
	}

	@Test
	@WithMockUser(username = "testuser")
	void testGetAllJobApplicationsForUser() throws Exception {
		// Mocking service layer methods
		when(userService.getUserByUsername("testuser")).thenReturn(mockUser);
		when(jobApplicationService.getAllApplicationsByUser(mockUser.getId())).thenReturn(List.of(mockJobApplication));

		// Performing the GET request and verifying the response
		mockMvc.perform(get("/api/job-applications/user")).andExpect(status().isOk()) // Check if status is 200 OK
				.andExpect(jsonPath("$[0].company").value("Company A")); // Verify the company name in the response

		// Verifying that the services were called as expected
		verify(userService).getUserByUsername("testuser");
		verify(jobApplicationService).getAllApplicationsByUser(mockUser.getId());
	}

	@Test
	@WithMockUser(username = "testuser")
	void testCreateJobApplication() throws Exception {
		// Mocking service layer methods
		when(userService.getUserByUsername("testuser")).thenReturn(mockUser);
		when(jobApplicationService.saveApplication(any(JobApplication.class))).thenReturn(mockJobApplication);

		// Performing the POST request and verifying the response
		mockMvc.perform(post("/api/job-applications/create").contentType("application/json")
				.content(objectMapper.writeValueAsString(mockJobApplication))).andExpect(status().isOk())
				.andExpect(jsonPath("$.company").value("Company A"));

		// Verifying that the services were called as expected
		verify(userService).getUserByUsername("testuser");
		verify(jobApplicationService).saveApplication(any(JobApplication.class));
	}

	@Test
	@WithMockUser(username = "testuser")
	void testUpdateJobApplication() throws Exception {
		// Mocking service layer methods
		when(userService.getUserByUsername("testuser")).thenReturn(mockUser);
		when(jobApplicationService.getAllApplicationsByUser(mockUser.getId())).thenReturn(List.of(mockJobApplication));
		when(jobApplicationService.updateApplication(eq(mockJobApplication.getId()), any(JobApplication.class)))
				.thenReturn(mockJobApplication);

		// Performing the PUT request and verifying the response
		mockMvc.perform(put("/api/job-applications/update/1").contentType("application/json")
				.content(objectMapper.writeValueAsString(mockJobApplication))).andExpect(status().isOk())
				.andExpect(jsonPath("$.company").value("Company A"));

		// Verifying that the services were called as expected
		verify(userService).getUserByUsername("testuser");
		verify(jobApplicationService).updateApplication(eq(mockJobApplication.getId()), any(JobApplication.class));
	}

	@Test
	@WithMockUser(username = "testuser")
	void testDeleteJobApplication() throws Exception {
		// Mocking service layer methods
		when(userService.getUserByUsername("testuser")).thenReturn(mockUser);
		when(jobApplicationService.getAllApplicationsByUser(mockUser.getId())).thenReturn(List.of(mockJobApplication));

		// Performing the DELETE request and verifying the response
		mockMvc.perform(delete("/api/job-applications/delete/1")).andExpect(status().isOk())
				.andExpect(content().string("Application deleted successfully"));

		// Verifying that the services were called as expected
		verify(userService).getUserByUsername("testuser");
		verify(jobApplicationService).deleteApplication(mockJobApplication.getId());
	}

	@Test
	@WithMockUser(username = "testuser")
	void testFilterJobApplications() throws Exception {
		// Mocking service layer methods
		List<JobApplication> applications = List.of(mockJobApplication);
		when(jobApplicationService.filterJobApplications("Company A", null, null)).thenReturn(applications);

		// Performing the GET request with filter parameters and verifying the response
		mockMvc.perform(get("/api/job-applications/filter").param("company", "Company A")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].company").value("Company A"));

		// Verifying that the service method was called as expected
		verify(jobApplicationService).filterJobApplications("Company A", null, null);
	}
}
