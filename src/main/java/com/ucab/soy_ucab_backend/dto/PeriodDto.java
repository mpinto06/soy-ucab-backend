package com.ucab.soy_ucab_backend.dto;

import java.time.LocalDate;
import java.util.List;

public record PeriodDto(
    String id,
    String type, // "academic" or "professional"
    LocalDate startDate,
    LocalDate endDate,
    String description,
    List<String> skills,
    
    // Academic specific
    String degreeId,
    String title,
    
    // Professional specific
    String organization,
    String positionType,
    String position,
    
    // File upload
    String fileBase64,
    String fileFormat
) {}
