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
public class OfertasAplicadasService {

    @Value("${jsreport.server.url:http://localhost:5488}")
    private String jsReportUrl;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

    public byte[] generateOfertasReport(String email) throws IOException {
        // Validate if user is an Organization
        String countSql = "SELECT COUNT(*) FROM Organizacion WHERE correo_electronico = ?";
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class, email);
        if (count != null && count > 0) {
            throw new RuntimeException("Las organizaciones no pueden generar reportes de ofertas aplicadas.");
        }

        // 1. Fetch User Data
        String userSql = "SELECT primer_nombre || ' ' || primer_apellido as nombre FROM Persona WHERE correo_electronico = ?";
        String userName;
        try {
            userName = jdbcTemplate.queryForObject(userSql, String.class, email);
        } catch (Exception e) {
            userName = "Usuario"; // Fallback or handle error
        }

        // 2. Fetch Saved Offers
        String savedSql = """
                SELECT o.nombre_organizacion as empresa, ot.nombre_cargo as cargo,
                       CAST(ot.tipo_cargo AS text) as tipo_cargo, CAST(ot.modalidad AS text) as modalidad,
                       ot.ubicacion, ot.descripcion_cargo, TO_CHAR(ot.fecha_publicacion, 'DD Mon YYYY') as fecha_publicacion,
                       m.archivo_foto, m.formato_foto
                FROM Guarda g
                JOIN Oferta_Trabajo ot ON g.correo_publicador = ot.correo_publicador AND g.nombre_cargo = ot.nombre_cargo
                JOIN Organizacion o ON ot.correo_publicador = o.correo_electronico
                LEFT JOIN Miembro m ON o.correo_electronico = m.correo_electronico
                WHERE g.correo_persona = ?
                """;
        List<Map<String, Object>> savedOffers = jdbcTemplate.queryForList(savedSql, email);
        processOffers(savedOffers);

        // 3. Fetch Applied Offers
        String appliedSql = """
                SELECT o.nombre_organizacion as empresa, ot.nombre_cargo as cargo,
                       CAST(ot.tipo_cargo AS text) as tipo_cargo, CAST(ot.estado_oferta AS text) as estado,
                       TO_CHAR(a.fecha_aplicacion, 'DD Mon YYYY') as fecha_aplicacion,
                       ot.ubicacion, ot.descripcion_cargo, CAST(ot.modalidad AS text) as modalidad,
                       m.archivo_foto, m.formato_foto
                FROM Aplica a
                JOIN Oferta_Trabajo ot ON a.correo_publicador = ot.correo_publicador AND a.nombre_cargo = ot.nombre_cargo
                JOIN Organizacion o ON ot.correo_publicador = o.correo_electronico
                LEFT JOIN Miembro m ON o.correo_electronico = m.correo_electronico
                WHERE a.correo_aplicante = ?
                """;
        List<Map<String, Object>> appliedOffers = jdbcTemplate.queryForList(appliedSql, email);
        processOffers(appliedOffers);

        // 4. Build Data Map
        Map<String, Object> data = new HashMap<>();
        data.put("usuario", userName);
        data.put("correo", email);
        data.put("fecha_generacion", LocalDate.now().toString());
        data.put("guardadas_count", savedOffers.size());
        data.put("aplicadas_count", appliedOffers.size());
        data.put("guardadas", savedOffers);
        data.put("aplicadas", appliedOffers);

        // 5. Generate PDF
        return generateReportPdf("ofertas_report.html", data);
    }

    private void processOffers(List<Map<String, Object>> offers) {
        for (Map<String, Object> offer : offers) {
            // Company Initial
            String empresa = (String) offer.get("empresa");
            if (empresa != null && !empresa.isEmpty()) {
                offer.put("initial", empresa.substring(0, 1).toUpperCase());
            } else {
                offer.put("initial", "?");
            }

            // Process Image
            byte[] photoBytes = (byte[]) offer.get("archivo_foto");
            if (photoBytes != null && photoBytes.length > 0) {
                String format = (String) offer.get("formato_foto");
                if (format == null)
                    format = "png"; // fallback
                String base64 = java.util.Base64.getEncoder().encodeToString(photoBytes);
                offer.put("logo_base64", "data:image/" + format + ";base64," + base64);
            } else {
                // Return null or path to a default image if desired, handled in template
                offer.put("logo_base64", null);
            }

            // Process Mock Status for Applied Offers if 'estado' is present but we want
            // 'estado_aplicacion'
            if (offer.containsKey("estado")) {
                // For now, mapping 'abierta' to 'En proceso' and 'cerrada' to 'Cerrada' or
                // similar for visual badging
                String estadoOferta = (String) offer.get("estado");
                if ("abierta".equalsIgnoreCase(estadoOferta)) {
                    offer.put("estado_badge", "En proceso");
                    offer.put("estado_class", "badge-process");
                } else {
                    offer.put("estado_badge", "Cerrada");
                    offer.put("estado_class", "badge-closed");
                }
            }
        }
    }

    private byte[] generateReportPdf(String templateFileName, Map<String, Object> data) throws IOException {
        ClassPathResource templateResource = new ClassPathResource("reports/" + templateFileName);
        String templateContent = new String(templateResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // Prepare request for JSReport
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

        // Adjust URL for API
        String apiUrl = jsReportUrl + "/api/report";

        ResponseEntity<byte[]> response = restTemplate.postForEntity(apiUrl, request, byte[].class);

        return response.getBody();
    }
}
