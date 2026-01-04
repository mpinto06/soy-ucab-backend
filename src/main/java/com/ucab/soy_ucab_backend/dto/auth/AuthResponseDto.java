package com.ucab.soy_ucab_backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String email;
    private String role;
    private String token; // Can be dummy for now or just userId
}
