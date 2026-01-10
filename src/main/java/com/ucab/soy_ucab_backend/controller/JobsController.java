package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.service.JobService;
import com.ucab.soy_ucab_backend.service.OfertasAplicadasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jobs")
@CrossOrigin(origins = "http://localhost:4200")
public class JobsController {

    @Autowired
    private OfertasAplicadasService ofertasAplicadasService;

    @Autowired
    private JobService jobService;

    @GetMapping("/user-data")
    public ResponseEntity<Map<String, Object>> getUserJobs(@RequestParam String email) {
        try {
            Map<String, Object> data = ofertasAplicadasService.getOfertasData(email);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllJobs(@RequestParam String email) {
        try {
            return ResponseEntity.ok(jobService.getAllOffers(email));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> toggleSaveJob(@RequestBody Map<String, String> payload) {
        try {
            jobService.toggleSaveJob(
                    payload.get("userEmail"),
                    payload.get("publisherEmail"),
                    payload.get("jobTitle"));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/apply", consumes = "multipart/form-data")
    public ResponseEntity<?> applyToJob(
            @RequestParam("userEmail") String userEmail,
            @RequestParam("publisherEmail") String publisherEmail,
            @RequestParam("jobTitle") String jobTitle,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "cv", required = false) org.springframework.web.multipart.MultipartFile cv) {
        try {
            jobService.applyToJob(
                    userEmail,
                    publisherEmail,
                    jobTitle,
                    description,
                    cv);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = e.getMessage();
            if (errorMessage == null)
                errorMessage = "Error desconocido";

            // Clean up typical Spring/JDBC error wrappings if possible, or just send it
            // all.
            // But user wants specific message. The SP message usually appears in the cause.
            // Let's rely on e.getMessage() usually containing the nested exception message
            // in Spring's DataAccessException.

            // Return 400 Bad Request with the message
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", errorMessage));
        }
    }

    @PostMapping("/cancel-application")
    public ResponseEntity<?> cancelApplication(@RequestBody Map<String, String> payload) {
        try {
            jobService.cancelApplication(
                    payload.get("userEmail"),
                    payload.get("publisherEmail"),
                    payload.get("jobTitle"));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/published")
    public ResponseEntity<List<Map<String, Object>>> getPublishedJobs(@RequestParam String email) {
        try {
            return ResponseEntity.ok(jobService.getOrganizationOffers(email));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createJob(@RequestBody Map<String, String> payload) {
        try {
            jobService.createJobOffer(
                    payload.get("publisherEmail"),
                    payload.get("title"),
                    payload.get("description"),
                    payload.get("type"),
                    payload.get("modality"),
                    payload.get("location"));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = e.getMessage();
            if (errorMessage != null
                    && (errorMessage.contains("duplicate key") || errorMessage.contains("unique constraint"))) {
                errorMessage = "Ya existe una oferta con este título ('" + payload.get("title")
                        + "'). Por favor elige otro título.";
            } else if (errorMessage == null) {
                errorMessage = "Error creando la oferta";
            }
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", errorMessage));
        }
    }

    @GetMapping("/check-role")
    public ResponseEntity<Map<String, Boolean>> checkRole(@RequestParam String email) {
        boolean isOrg = jobService.isOrganization(email);
        return ResponseEntity.ok(java.util.Collections.singletonMap("isOrg", isOrg));
    }

    @GetMapping("/applicants")
    public ResponseEntity<List<Map<String, Object>>> getJobApplicants(@RequestParam String publisherEmail,
            @RequestParam String jobTitle) {
        try {
            return ResponseEntity.ok(jobService.getJobApplicants(publisherEmail, jobTitle));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
