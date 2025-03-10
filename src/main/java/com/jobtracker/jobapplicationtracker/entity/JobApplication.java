package com.jobtracker.jobapplicationtracker.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(max = 255)
	private String company; // Renamed for consistency

	@NotNull
	@Size(max = 255)
	private String role;

	private LocalDate applicationDate; // Stores the date of application

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ApplicationStatus status; // Enum for status

	@Column(length = 1000)
	private String notes; // Additional notes about the application

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user; // Links the application to a specific user

	public enum ApplicationStatus {
		INTERVIEWING, REJECTED, OFFERED, PENDING
	}
}
