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
        // 1. Fetch Organization Info (Name & Type)
        String orgInfoSql = """
                SELECT o.nombre_organizacion,
                       CASE
                           WHEN d.correo_electronico IS NOT NULL THEN 'Dependencia UCAB'
                           WHEN oa.correo_electronico IS NOT NULL THEN 'Organización Asociada'
                           ELSE 'Organización'
                       END as tipo
                FROM Organizacion o
                LEFT JOIN Dependencia_UCAB d ON o.correo_electronico = d.correo_electronico
                LEFT JOIN Organizacion_Asociada oa ON o.correo_electronico = oa.correo_electronico
                WHERE o.correo_electronico = ?
                """;

        Map<String, Object> orgInfo;
        try {
            orgInfo = jdbcTemplate.queryForMap(orgInfoSql, email);
        } catch (Exception e) {
            throw new RuntimeException("La organización no existe o no es válida.");
        }

        // 2. Fetch Offers for this specific Organization
        String offersSql = """
                SELECT ot.nombre_cargo as cargo,
                       CAST(ot.tipo_cargo AS text) as tipo_cargo,
                       CAST(ot.modalidad AS text) as modalidad,
                       ot.ubicacion,
                       CAST(ot.estado_oferta AS text) as estado,
                       TO_CHAR(ot.fecha_publicacion, 'DD-MM-YYYY') as fecha_publicacion
                FROM Oferta_Trabajo ot
                WHERE ot.correo_publicador = ?
                """;
        List<Map<String, Object>> offers = jdbcTemplate.queryForList(offersSql, email);

        // 3. Build Data Map
        Map<String, Object> data = new HashMap<>();
        data.put("fecha_generacion", LocalDate.now().toString());
        data.put("nombre_organizacion", orgInfo.get("nombre_organizacion"));
        data.put("tipo_organizacion", orgInfo.get("tipo"));
        data.put("correo", email);
        data.put("ofertas", offers);

        // 4. Generate PDF
        return generateReportPdf("ofertas_ofrecidas.html", data);
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
