package com.ucab.soy_ucab_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {

    @Value("${jsreport.server.url}")
    private String jsReportUrl;

    private Map<String, String> reportTemplates;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void loadReportConfiguration() throws IOException {
        ClassPathResource resource = new ClassPathResource("reports/reports.json");
        JsonNode root = objectMapper.readTree(resource.getInputStream());
        reportTemplates = new HashMap<>();
        root.fields().forEachRemaining(entry -> reportTemplates.put(entry.getKey(), entry.getValue().asText()));
    }

    public byte[] generateSampleReport() throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Sample Report");
        data.put("date", LocalDate.now().toString());

        return generateReportPdf("sample", data);
    }

    public byte[] generateMemberDetail1(String email) throws IOException {
        // Mock data for now, printing email as requested
        System.out.println("Generating report for email: " + email);
        
        Map<String, Object> data = new HashMap<>();
        data.put("title", "Member Detail Report");
        data.put("email", email);
        data.put("date", LocalDate.now().toString());

        return generateReportPdf("memberDetail1", data);
    }

    private byte[] generateReportPdf(String reportName, Map<String, Object> data) throws IOException {
        if (!reportTemplates.containsKey(reportName)) {
            throw new IllegalArgumentException("Report not found: " + reportName);
        }

        String templateFileName = reportTemplates.get(reportName);
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
