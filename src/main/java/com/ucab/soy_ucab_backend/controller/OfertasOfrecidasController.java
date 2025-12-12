package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.service.OfertasOfrecidasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports/ofertas")
public class OfertasOfrecidasController {

    @Autowired
    private OfertasOfrecidasService ofertasOfrecidasService;

    @PostMapping("/ofrecidas")
    public ResponseEntity<byte[]> generateOfertasOfrecidasReport(@RequestBody String email) {
        try {
            String cleanEmail = email.replace("\"", "").trim();
            byte[] reportData = ofertasOfrecidasService.generateOfertasOfrecidasReport(cleanEmail);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_entidades.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(reportData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(("Error: " + e.getMessage()).getBytes());
        }
    }
}
