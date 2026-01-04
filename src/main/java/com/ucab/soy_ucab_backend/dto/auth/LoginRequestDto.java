package com.ucab.soy_ucab_backend.dto.auth;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
