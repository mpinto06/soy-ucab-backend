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

}
