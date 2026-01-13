package com.ucab.soy_ucab_backend.dto.auth;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterUserRequestDto {
    private String email;
    private String password;
    private String firstName;
    private String secondName;
    private String lastName;
    private String secondLastName;
    private String gender;
    private LocalDate birthDate;
}
