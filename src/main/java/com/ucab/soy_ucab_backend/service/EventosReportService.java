package com.ucab.soy_ucab_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventosReportService {

    @Value("${jsreport.server.url:http://localhost:5488}")
    private String jsReportUrl;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

    public byte[] generateEventosGuardadosReport(String email) throws IOException {
        // Validation: Organizations cannot generate this report
        String orgCheckSql = "SELECT COUNT(*) FROM Organizacion WHERE correo_electronico = ?";
        Integer count = jdbcTemplate.queryForObject(orgCheckSql, Integer.class, email);
        if (count != null && count > 0) {
            throw new RuntimeException("Las organizaciones no pueden generar reportes de eventos guardados.");
        }

        // 1. Fetch User Data
        String userSql = "SELECT primer_nombre || ' ' || primer_apellido as nombre FROM Persona WHERE correo_electronico = ?";
        String userName;
        try {
            userName = jdbcTemplate.queryForObject(userSql, String.class, email);
        } catch (Exception e) {
            userName = "Usuario";
        }

        // 2. Fetch Saved/Interested Events
        String eventsSql = """
                SELECT e.nombre_evento,
                       e.descripcion,
                       CAST(e.modalidad AS text) as modalidad,
                       e.ubicacion,
                       CAST(e.estado_evento AS text) as estado,
                       o.nombre_organizacion as organizador,
                       TO_CHAR(e.fecha_inicio, 'DD-MM-YYYY HH24:MI') as fecha_inicio,
                       TO_CHAR(e.fecha_fin, 'DD-MM-YYYY HH24:MI') as fecha_fin
                FROM Muestra_Interes mi
                JOIN Evento e ON mi.nombre_evento = e.nombre_evento
                JOIN Organizacion o ON e.correo_organizador = o.correo_electronico
                WHERE mi.correo_miembro = ?
                """;
        List<Map<String, Object>> events = jdbcTemplate.queryForList(eventsSql, email);

        // 3. Build Data Map
        Map<String, Object> data = new HashMap<>();
        data.put("usuario", userName);
        data.put("correo", email);
        data.put("fecha_generacion", LocalDate.now().toString());
        data.put("eventos", events);

        // 4. Generate PDF
        return generateReportPdf("eventos_guardados_report.html", data);
    }

    private byte[] generateReportPdf(String templateFileName, Map<String, Object> data) throws IOException {
        ClassPathResource templateResource = new ClassPathResource("reports/" + templateFileName);
        String templateContent = new String(templateResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> template = new HashMap<>();
        template.put("content", templateContent);
        template.put("engine", "handlebars");
        template.put("recipe", "chrome-pdf");

        requestBody.put("template", template);
        requestBody.put("data", data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        String apiUrl = jsReportUrl + "/api/report";
        ResponseEntity<byte[]> response = restTemplate.postForEntity(apiUrl, request, byte[].class);

        return response.getBody();
    }
}
