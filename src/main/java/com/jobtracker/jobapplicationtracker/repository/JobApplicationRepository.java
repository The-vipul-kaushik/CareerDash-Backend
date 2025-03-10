package com.jobtracker.jobapplicationtracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jobtracker.jobapplicationtracker.entity.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
	List<JobApplication> findByUserId(Long userId);

	@Query("SELECT j FROM JobApplication j WHERE "
			+ "(:company IS NULL OR LOWER(j.company) LIKE LOWER(CONCAT('%', :company, '%'))) AND "
			+ "(:role IS NULL OR LOWER(j.role) LIKE LOWER(CONCAT('%', :role, '%'))) AND "
			+ "(:status IS NULL OR j.status = :status)")
	List<JobApplication> filterJobApplications(@Param("company") String company, @Param("role") String role,
			@Param("status") JobApplication.ApplicationStatus status);
}
