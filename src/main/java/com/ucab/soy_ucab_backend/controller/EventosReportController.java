package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.service.EventosReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports/eventos")
public class EventosReportController {

    @Autowired
    private EventosReportService eventosReportService;

    @PostMapping("/guardados")
    public ResponseEntity<byte[]> generateEventosGuardadosReport(@RequestBody String email) {
        try {
            String cleanEmail = email.replace("\"", "").trim();
            byte[] reportData = eventosReportService.generateEventosGuardadosReport(cleanEmail);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_eventos_interes.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(reportData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(("Error: " + e.getMessage()).getBytes());
        }
    }
}
