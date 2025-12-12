package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.service.OfertasAplicadasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/reports/ofertas")
public class OfertasAplicadasController {

    @Autowired
    private OfertasAplicadasService ofertasAplicadasService;

    @PostMapping("/aplicadas")
    public ResponseEntity<byte[]> generateOfertasReport(@RequestBody String email) {
        try {
            // Simple email cleanup if sent as plain text with quotes or json string
            String cleanEmail = email.replace("\"", "").trim();

            byte[] reportData = ofertasAplicadasService.generateOfertasReport(cleanEmail);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_ofertas.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(reportData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(("Error: " + e.getMessage()).getBytes());
        }
    }

}
