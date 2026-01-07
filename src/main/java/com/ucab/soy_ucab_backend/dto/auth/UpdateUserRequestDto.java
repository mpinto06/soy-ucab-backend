package com.ucab.soy_ucab_backend.dto.auth;

import java.time.LocalDate;
import java.util.List;

public record UpdateUserRequestDto(
    String firstName,
    String lastName,
    String gender,
    String location,
    String profileHeader,
    String profileImageBase64,
    List<String> interests
) {}
