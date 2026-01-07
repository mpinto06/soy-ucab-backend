package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.dto.auth.AuthResponseDto;
import com.ucab.soy_ucab_backend.dto.auth.LoginRequestDto;
import com.ucab.soy_ucab_backend.dto.auth.RegisterOrgRequestDto;
import com.ucab.soy_ucab_backend.dto.auth.RegisterUserRequestDto;
import com.ucab.soy_ucab_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register/user")
    public ResponseEntity<AuthResponseDto> registerUser(@RequestBody RegisterUserRequestDto request) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PostMapping("/register/org")
    public ResponseEntity<AuthResponseDto> registerOrg(@RequestBody RegisterOrgRequestDto request) {
        return ResponseEntity.ok(authService.registerOrg(request));
    }

    @PostMapping("/update")
    public ResponseEntity<AuthResponseDto> updateUser(@RequestBody com.ucab.soy_ucab_backend.dto.auth.UpdateUserRequestDto request) {
        // TODO: Get email from SecurityContext. For now, we unfortunately need the email in the request or assume single user dev mode.
        // Since I cannot change DTO structure easily without affecting user request logic:
        // "This service it's going to receive a similar DTO... everything it's going to be null except for the new fields."
        // I'll grab the email from the SecurityContext assuming JwtFilter is working and setting Authentication.
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(authService.updateUser(email, request));
    }

    @PostMapping("/interest/add")
    public ResponseEntity<AuthResponseDto> addInterest(@RequestParam String interest) {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(authService.addInterest(email, interest));
    }

    @PostMapping("/interest/remove")
    public ResponseEntity<AuthResponseDto> removeInterest(@RequestParam String interest) {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(authService.removeInterest(email, interest));
    }

    @DeleteMapping("/period")
    public ResponseEntity<AuthResponseDto> deletePeriod(@RequestParam String id, @RequestParam String type) {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(authService.deletePeriod(email, id, type));
    }
}
