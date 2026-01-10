package com.ucab.soy_ucab_backend.dto.auth;

import java.time.LocalDate;


public record UpdateUserRequestDto(
    String email,
    String firstName,
    String lastName,
    String gender,
    String location,
    String profileHeader,
    String profileImageBase64,
    String imageFormat,
    String imageName,
    String newEmail
) {}
