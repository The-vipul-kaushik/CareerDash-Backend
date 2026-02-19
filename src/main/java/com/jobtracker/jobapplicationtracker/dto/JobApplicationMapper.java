package com.jobtracker.jobapplicationtracker.dto;

import com.jobtracker.jobapplicationtracker.entity.JobApplication;

public class JobApplicationMapper {

    public static JobApplicationDTO toDTO(JobApplication entity) {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setId(entity.getId());
        dto.setCompany(entity.getCompany());
        dto.setRole(entity.getRole());
        dto.setStatus(entity.getStatus().name());
        dto.setNotes(entity.getNotes());
        dto.setApplicationDate(entity.getApplicationDate());
        return dto;
    }
}
