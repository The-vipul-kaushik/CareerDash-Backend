package com.jobtracker.jobapplicationtracker.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class JobApplicationDTO {
    private Long id;
    private String company;
    private String role;
    private String status;
    private String notes;
    private LocalDate applicationDate;
}
