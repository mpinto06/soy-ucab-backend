package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/sample")
    public ResponseEntity<byte[]> generateSampleReport() {
        try {
            byte[] reportData = reportService.generateSampleReport();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample_report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(reportData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/member-detail")
    public ResponseEntity<byte[]> generateMemberDetail1(@RequestBody String email) {
        try {
            byte[] reportData = reportService.generateMemberDetail1(email);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=member_detail_1.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(reportData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/people-from-org")
    public ResponseEntity<byte[]> generatePeopleFromOrg2(@RequestBody String orgEmail) {
        try {
            byte[] reportData = reportService.generatePeopleFromOrg2(orgEmail);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=people_from_org.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(reportData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
