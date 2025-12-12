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

        // 2. Fetch Saved/Interested Events with Stats and Logo
        String eventsSql = """
                SELECT e.nombre_evento,
                       e.descripcion,
                       CAST(e.modalidad AS text) as modalidad,
                       e.ubicacion,
                       e.url_conferencia,
                       CAST(e.estado_evento AS text) as estado,
                       o.nombre_organizacion as organizador,
                       e.fecha_inicio,
                       e.fecha_fin,
                       CAST(m.archivo_foto AS bytea) as archivo_foto,
                       CAST(m.formato_foto AS text) as formato_foto,
                       (SELECT COUNT(*) FROM Muestra_Interes mi2 WHERE mi2.nombre_evento = e.nombre_evento) as interesados,
                       (SELECT COUNT(*) FROM Asiste a WHERE a.nombre_evento = e.nombre_evento) as asistentes
                FROM Muestra_Interes mi
                JOIN Evento e ON mi.nombre_evento = e.nombre_evento
                JOIN Organizacion o ON e.correo_organizador = o.correo_electronico
                LEFT JOIN Miembro m ON o.correo_electronico = m.correo_electronico
                WHERE mi.correo_miembro = ?
                """;
        List<Map<String, Object>> events = jdbcTemplate.queryForList(eventsSql, email);

        // Process events
        for (Map<String, Object> event : events) {
            // Modality / Location
            String modalidad = (String) event.get("modalidad");
            if ("virtual".equalsIgnoreCase(modalidad)) {
                String url = (String) event.get("url_conferencia");
                event.put("ubicacion_display", "Plataforma Virtual");
                event.put("badge_text", "Virtual");
            } else {
                String ubicacion = (String) event.get("ubicacion");
                event.put("ubicacion_display", ubicacion != null ? ubicacion : "TBD");
                event.put("badge_text", "Presencial");
            }
            // Add a secondary badge for type (Mocking 'Webinar' or 'Taller' based on
            // description/random)
            event.put("category_badge", "Evento");

            // Date Formatting
            java.sql.Timestamp startTs = (java.sql.Timestamp) event.get("fecha_inicio");
            java.sql.Timestamp endTs = (java.sql.Timestamp) event.get("fecha_fin");

            if (startTs != null) {
                // "martes, 14 de octubre de 2025"
                java.time.LocalDateTime start = startTs.toLocalDateTime();
                java.time.format.DateTimeFormatter dateFmt = java.time.format.DateTimeFormatter
                        .ofPattern("EEEE, d 'de' MMMM 'de' yyyy", java.util.Locale.forLanguageTag("es"));
                event.put("fecha_formateada", start.format(dateFmt));

                // "16:00 - 18:00"
                java.time.format.DateTimeFormatter timeFmt = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
                String duracion = start.format(timeFmt);
                if (endTs != null) {
                    duracion += " - " + endTs.toLocalDateTime().format(timeFmt);
                }
                event.put("hora_formateada", duracion);
            }

            // Logo Processing
            processLogo(event);

            // Generate a random gradient for the banner since we don't have event images
            event.put("banner_gradient", getRandomGradient());
        }

        // 3. Build Data Map
        Map<String, Object> data = new HashMap<>();
        data.put("usuario", userName);
        data.put("correo", email);
        data.put("fecha_generacion", LocalDate.now().toString());
        data.put("eventos", events);

        // 4. Generate PDF
        return generateReportPdf("eventos_guardados_report.html", data);
    }

    private void processLogo(Map<String, Object> data) {
        String name = (String) data.get("organizador");
        if (name != null && !name.isEmpty()) {
            data.put("initial", name.substring(0, 1).toUpperCase());
        } else {
            data.put("initial", "?");
        }

        byte[] photoBytes = (byte[]) data.get("archivo_foto");
        if (photoBytes != null && photoBytes.length > 0) {
            String format = (String) data.get("formato_foto");
            if (format == null)
                format = "png";
            String base64 = java.util.Base64.getEncoder().encodeToString(photoBytes);
            data.put("logo_base64", "data:image/" + format + ";base64," + base64);
        } else {
            data.put("logo_base64", null);
        }
    }

    private String getRandomGradient() {
        String[] gradients = {
                "linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%)",
                "linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)",
                "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
                "linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%)",
                "linear-gradient(135deg, #e0c3fc 0%, #8ec5fc 100%)"
        };
        return gradients[(int) (Math.random() * gradients.length)];
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
