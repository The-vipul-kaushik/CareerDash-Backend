package com.jobtracker.jobapplicationtracker.service;

import com.jobtracker.jobapplicationtracker.dto.JobApplicationDTO;
import com.jobtracker.jobapplicationtracker.entity.JobApplication;
import com.jobtracker.jobapplicationtracker.entity.User;
import com.jobtracker.jobapplicationtracker.repository.JobApplicationRepository;
import com.jobtracker.jobapplicationtracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobApplicationServiceImplTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobApplicationServiceImpl jobApplicationService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("vipul");
    }

    @Test
    void testCreateApplicationDefaultsToApplied() {
        JobApplication app = new JobApplication();
        app.setCompany("Accenture");

        when(userRepository.findByUsername("vipul")).thenReturn(Optional.of(user));
        when(jobApplicationRepository.save(any(JobApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobApplicationDTO result = jobApplicationService.createApplication(app, "vipul");

        assertEquals("APPLIED", result.getStatus());
        assertEquals("Accenture", result.getCompany());
    }

    @Test
    void testGetApplicationForUserSuccess() {
        JobApplication app = new JobApplication();
        app.setId(1L);
        app.setCompany("Accenture");
        app.setUser(user);
        app.setStatus(JobApplication.ApplicationStatus.APPLIED);

        when(userRepository.findByUsername("vipul")).thenReturn(Optional.of(user));
        when(jobApplicationRepository.findById(1L)).thenReturn(Optional.of(app));

        JobApplicationDTO result = jobApplicationService.getApplicationForUser(1L, "vipul");

        assertEquals("Accenture", result.getCompany());
        assertEquals("APPLIED", result.getStatus());
    }

    @Test
    void testGetApplicationForUserForbidden() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("other");

        JobApplication app = new JobApplication();
        app.setId(1L);
        app.setCompany("Accenture");
        app.setUser(otherUser);

        when(userRepository.findByUsername("vipul")).thenReturn(Optional.of(user));
        when(jobApplicationRepository.findById(1L)).thenReturn(Optional.of(app));

        assertThrows(SecurityException.class, () -> jobApplicationService.getApplicationForUser(1L, "vipul"));
    }

    @Test
    void testUpdateApplicationSuccess() {
        JobApplication existingApp = new JobApplication();
        existingApp.setId(1L);
        existingApp.setCompany("Accenture");
        existingApp.setUser(user);

        JobApplication updatedApp = new JobApplication();
        updatedApp.setCompany("Infosys");
        updatedApp.setRole("Backend Engineer");
        updatedApp.setApplicationDate(LocalDate.now());
        updatedApp.setStatus(JobApplication.ApplicationStatus.INTERVIEWING);
        updatedApp.setNotes("Technical round");

        when(jobApplicationRepository.findById(1L)).thenReturn(Optional.of(existingApp));
        when(jobApplicationRepository.save(any(JobApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobApplicationDTO result = jobApplicationService.updateApplication(1L, updatedApp, "vipul");

        assertEquals("Infosys", result.getCompany());
        assertEquals("Backend Engineer", result.getRole());
        assertEquals("INTERVIEWING", result.getStatus());
    }

    @Test
    void testDeleteApplicationSuccess() {
        JobApplication existingApp = new JobApplication();
        existingApp.setId(1L);
        existingApp.setCompany("Accenture");
        existingApp.setUser(user);

        when(jobApplicationRepository.findById(1L)).thenReturn(Optional.of(existingApp));

        jobApplicationService.deleteApplication(1L, "vipul");

        verify(jobApplicationRepository, times(1)).delete(existingApp);
    }

    @Test
    void testFilterJobApplicationsValidStatus() {
        JobApplication app = new JobApplication();
        app.setId(1L);
        app.setCompany("Accenture");
        app.setRole("Java Developer");
        app.setStatus(JobApplication.ApplicationStatus.APPLIED);
        app.setUser(user);

        when(userRepository.findByUsername("vipul")).thenReturn(Optional.of(user));
        when(jobApplicationRepository.filterJobApplications(eq(1L), eq("Accenture"), eq("Java Developer"), eq(JobApplication.ApplicationStatus.APPLIED)))
                .thenReturn(List.of(app));

        List<JobApplicationDTO> results = jobApplicationService.filterJobApplications("Accenture", "Java Developer", "APPLIED", "vipul");

        assertEquals(1, results.size());
        assertEquals("Accenture", results.get(0).getCompany());
    }

    @Test
    void testFilterJobApplicationsInvalidStatus() {
        when(userRepository.findByUsername("vipul")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> jobApplicationService.filterJobApplications("Accenture", "Java Developer", "INVALID", "vipul"));
    }
}