package com.ucab.soy_ucab_backend.dto.auth;

import lombok.Data;

@Data
public class RegisterOrgRequestDto {
    private String email;
    private String password;
    private String orgName;
    private String organizationType;
    
    // UCAB
    private String ucabEntityType; // escuela, facultad, etc
    private String school;
    private String faculty;
    
    // Organizacion Externa
    private String rif;
    private String description;
}
