package com.ucab.soy_ucab_backend.dto.auth;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String email;
    private String role;
    private String memberType;
    private Object memberDetails;

    public AuthResponseDto(String email, String role, String memberType, Object memberDetails) {
        this.email = email;
        this.role = role;
        this.memberType = memberType;
        this.memberDetails = memberDetails;
    }
}
