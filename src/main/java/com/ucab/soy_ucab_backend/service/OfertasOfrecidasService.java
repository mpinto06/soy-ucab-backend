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
public class OfertasOfrecidasService {

    @Value("${jsreport.server.url:http://localhost:5488}")
    private String jsReportUrl;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

    public byte[] generateOfertasOfrecidasReport(String email) throws IOException {
        // 1. Fetch Organization Info (Name & Type & Logo)
        String orgInfoSql = """
                SELECT o.nombre_organizacion,
                       CAST(m.archivo_foto AS bytea) as archivo_foto,
                       CAST(m.formato_foto AS text) as formato_foto,
                       CASE
                           WHEN d.correo_electronico IS NOT NULL THEN 'Dependencia UCAB'
                           WHEN oa.correo_electronico IS NOT NULL THEN 'Organización Asociada'
                           ELSE 'Organización'
                       END as tipo
                FROM Organizacion o
                LEFT JOIN Miembro m ON o.correo_electronico = m.correo_electronico
                LEFT JOIN Dependencia_UCAB d ON o.correo_electronico = d.correo_electronico
                LEFT JOIN Organizacion_Asociada oa ON o.correo_electronico = oa.correo_electronico
                WHERE o.correo_electronico = ?
                """;

        Map<String, Object> orgInfo;
        try {
            orgInfo = jdbcTemplate.queryForMap(orgInfoSql, email);
            processLogo(orgInfo);
        } catch (Exception e) {
            throw new RuntimeException("La organización no existe o no es válida.");
        }

        // 2. Fetch Offers for this specific Organization with Applicant Counts
        String offersSql = """
                SELECT ot.nombre_cargo as cargo,
                       CAST(ot.tipo_cargo AS text) as tipo_cargo,
                       CAST(ot.modalidad AS text) as modalidad,
                       ot.ubicacion,
                       CAST(ot.estado_oferta AS text) as estado,
                       ot.fecha_publicacion,
                       (SELECT COUNT(*) FROM Aplica a
                        WHERE a.correo_publicador = ot.correo_publicador
                        AND a.nombre_cargo = ot.nombre_cargo) as aplicantes
                FROM Oferta_Trabajo ot
                WHERE ot.correo_publicador = ?
                """;
        List<Map<String, Object>> offers = jdbcTemplate.queryForList(offersSql, email);

        // 3. Process Offers (Mock Stats, Date Formatting)
        int totalOfertas = 0;
        int totalVistas = 0;
        int totalAplicantes = 0;

        for (Map<String, Object> offer : offers) {
            totalOfertas++;

            // Applicant count
            Number applicantsNum = (Number) offer.get("aplicantes");
            int applicants = (applicantsNum != null) ? applicantsNum.intValue() : 0;
            totalAplicantes += applicants;

            // Mock Views (Randomized between 100-500 + applicants*2 to be somewhat
            // realistic)
            int views = 100 + (int) (Math.random() * 400) + (applicants * 2);
            offer.put("vistas", views);
            totalVistas += views;

            // Conversion Rate
            double conversion = (views > 0) ? ((double) applicants / views) * 100 : 0;
            offer.put("tasa_conversion", String.format("%.1f%%", conversion));

            // Relative Date
            java.sql.Date sqlDate = (java.sql.Date) offer.get("fecha_publicacion");
            if (sqlDate != null) {
                LocalDate pubDate = sqlDate.toLocalDate();
                long days = java.time.temporal.ChronoUnit.DAYS.between(pubDate, LocalDate.now());
                if (days == 0)
                    offer.put("tiempo_publicacion", "Hoy");
                else if (days == 1)
                    offer.put("tiempo_publicacion", "Hace 1 día");
                else if (days < 7)
                    offer.put("tiempo_publicacion", "Hace " + days + " días");
                else if (days < 30)
                    offer.put("tiempo_publicacion", "Hace " + (days / 7) + " semanas");
                else
                    offer.put("tiempo_publicacion", "Hace " + (days / 30) + " meses");
            } else {
                offer.put("tiempo_publicacion", "Reciente");
            }

            // Badge Logic
            processBadge(offer);
        }

        // 4. Build Data Map
        Map<String, Object> data = new HashMap<>();
        data.put("fecha_generacion", LocalDate.now().toString());
        data.put("nombre_organizacion", orgInfo.get("nombre_organizacion"));
        data.put("logo_base64", orgInfo.get("logo_base64"));
        data.put("initial", orgInfo.get("initial"));
        data.put("tipo_organizacion", orgInfo.get("tipo"));
        data.put("total_ofertas", totalOfertas);
        data.put("total_vistas", totalVistas);
        data.put("total_aplicantes", totalAplicantes);
        data.put("ofertas", offers);

        // 5. Generate PDF
        return generateReportPdf("ofertas_ofrecidas.html", data);
    }

    private void processLogo(Map<String, Object> orgData) {
        // Initial fallback
        String name = (String) orgData.get("nombre_organizacion");
        if (name != null && !name.isEmpty()) {
            orgData.put("initial", name.substring(0, 1).toUpperCase());
        } else {
            orgData.put("initial", "?");
        }

        // Image Base64
        byte[] photoBytes = (byte[]) orgData.get("archivo_foto");
        if (photoBytes != null && photoBytes.length > 0) {
            String format = (String) orgData.get("formato_foto");
            if (format == null)
                format = "png";
            String base64 = java.util.Base64.getEncoder().encodeToString(photoBytes);
            orgData.put("logo_base64", "data:image/" + format + ";base64," + base64);
        } else {
            orgData.put("logo_base64", null);
        }
    }

    private void processBadge(Map<String, Object> offer) {
        String tipo = (String) offer.get("tipo_cargo");
        if (tipo == null)
            tipo = "Tiempo Completo";
        offer.put("badge_text", tipo);

        // Simple color mapping based on type
        switch (tipo) {
            case "Jornada Completa":
                offer.put("badge_class", "badge-blue");
                break;
            case "Jornada Parcial":
                offer.put("badge_class", "badge-purple");
                break;
            case "Contrato":
                offer.put("badge_class", "badge-orange");
                break;
            case "Pasantía":
                offer.put("badge_class", "badge-green");
                break;
            case "Voluntariado":
                offer.put("badge_class", "badge-pink");
                break;
            case "Miembro":
                offer.put("badge_class", "badge-cyan");
                break;
            default:
                offer.put("badge_class", "badge-gray");
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
