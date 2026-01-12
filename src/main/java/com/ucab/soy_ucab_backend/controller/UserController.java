package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.dto.PeriodDto;
import com.ucab.soy_ucab_backend.dto.auth.AuthResponseDto;
import com.ucab.soy_ucab_backend.dto.auth.UpdateUserRequestDto;

import com.ucab.soy_ucab_backend.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ProfileService profileService;

    @PostMapping("/update")
    public ResponseEntity<AuthResponseDto> updateUser(@RequestBody UpdateUserRequestDto request) {
        // Email is expected in the request body as 'email' (current email)
        return ResponseEntity.ok(profileService.updateUser(request.email(), request));
    }

    @PostMapping("/interest/add")
    public ResponseEntity<AuthResponseDto> addInterest(@RequestParam String email, @RequestParam String interest) {
        return ResponseEntity.ok(profileService.addInterest(email, interest));
    }

    @PostMapping("/interest/remove")
    public ResponseEntity<AuthResponseDto> removeInterest(@RequestParam String email, @RequestParam String interest) {
        return ResponseEntity.ok(profileService.removeInterest(email, interest));
    }

    @GetMapping("/interests")
    public ResponseEntity<java.util.List<String>> getAllInterests() {
        return ResponseEntity.ok(profileService.getAllInterests());
    }

    @GetMapping("/skills")
    public ResponseEntity<java.util.List<String>> getAllSkills() {
        return ResponseEntity.ok(profileService.getAllSkills());
    }

    @GetMapping("/organizations")
    public ResponseEntity<java.util.List<com.ucab.soy_ucab_backend.dto.OrganizationSummaryDto>> getAllOrganizations() {
        return ResponseEntity.ok(profileService.getAllOrganizations());
    }

    @GetMapping("/careers")
    public ResponseEntity<java.util.List<String>> getAllCareers() {
        return ResponseEntity.ok(profileService.getAllCareers());
    }

    @PostMapping("/period")
    public ResponseEntity<AuthResponseDto> savePeriod(@RequestParam String email, @RequestBody PeriodDto periodDto) {
        return ResponseEntity.ok(profileService.savePeriod(email, periodDto));
    }

    @DeleteMapping("/period")
    public ResponseEntity<AuthResponseDto> deletePeriod(@RequestParam String email, @RequestParam String id,
            @RequestParam String type) {
        return ResponseEntity.ok(profileService.deletePeriod(email, id, type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthResponseDto> getUserProfile(@PathVariable String id) {
        // The id here corresponds to the email since that's the PK
        return ResponseEntity.ok(profileService.getUserProfile(id));
    }
}
