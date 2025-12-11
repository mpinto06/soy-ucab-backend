package com.ucab.soy_ucab_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
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

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    public byte[] generateMemberDetail1(String email) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("date", LocalDate.now().toString());

        // 1. Fetch Basic Member Info
        String sqlMember = "SELECT m.correo_electronico, m.encabezado_perfil, m.nombre_archivo_foto, m.archivo_foto, m.formato_foto, " +
                "p.primer_nombre, p.segundo_nombre, p.primer_apellido, p.segundo_apellido, p.ubicacion_geografica, " +
                "o.nombre_organizacion, o.descripcion_org " +
                "FROM Miembro m " +
                "LEFT JOIN Persona p ON m.correo_electronico = p.correo_electronico " +
                "LEFT JOIN Organizacion o ON m.correo_electronico = o.correo_electronico " +
                "WHERE m.correo_electronico = ?";

        try {
            Map<String, Object> memberInfo = jdbcTemplate.queryForMap(sqlMember, email);
            data.put("member", memberInfo);
            data.put("profileHeadline", memberInfo.get("encabezado_perfil"));
            
            // Photo handling
            byte[] photoBytes = (byte[]) memberInfo.get("archivo_foto");
            String photoFormat = (String) memberInfo.get("formato_foto");
            
            if (photoBytes != null && photoFormat != null) {
                String base64Photo = java.util.Base64.getEncoder().encodeToString(photoBytes);
                data.put("photoBase64", "data:image/" + photoFormat + ";base64," + base64Photo);
            }

            // Initials logic
            String initials = "";

            // Format full name or organization name
            String name; 
            if (memberInfo.get("nombre_organizacion") != null) {
                name = (String) memberInfo.get("nombre_organizacion");
                data.put("isOrganization", true);
                data.put("description", memberInfo.get("descripcion_org"));
                
                if (name != null && !name.isEmpty()) {
                    initials = name.substring(0, 1).toUpperCase();
                }
            } else {
                String p1 = (String) memberInfo.get("primer_nombre");
                String p2 = (String) memberInfo.get("segundo_nombre");
                String a1 = (String) memberInfo.get("primer_apellido");
                String a2 = (String) memberInfo.get("segundo_apellido");
                name = p1 + " " + (p2 != null ? p2 + " " : "") + a1 + " " + (a2 != null ? a2 : "");
                data.put("isPerson", true);
                data.put("description", memberInfo.get("encabezado_perfil")); // Using encabezado as 'about' or description
                data.put("location", memberInfo.get("ubicacion_geografica"));

                if (p1 != null && !p1.isEmpty()) {
                    initials += p1.charAt(0);
                }
                if (a1 != null && !a1.isEmpty()) {
                    initials += a1.charAt(0);
                }
            }
            data.put("fullName", name);
            data.put("initials", initials.toUpperCase());

        } catch (Exception e) {
            throw new IllegalArgumentException("Member not found with email: " + email);
        }

        // 2. Counts (Followers, Friends, Views - Views ignored as per request)
        String sqlFollowers = "SELECT COUNT(*) FROM Sigue WHERE correo_seguido = ?";
        Integer followers = jdbcTemplate.queryForObject(sqlFollowers, Integer.class, email);
        data.put("followersCount", followers);

        if (data.containsKey("isPerson")) {
            String sqlFriends = "SELECT COUNT(*) FROM Es_Amigo WHERE (correo_persona1 = ? OR correo_persona2 = ?) AND estado = 'aceptada'";
            Integer friends = jdbcTemplate.queryForObject(sqlFriends, Integer.class, email, email);
            data.put("friendsCount", friends);
            
            // 3. Experience
            String sqlExp = "SELECT pe.cargo, pe.tipo_cargo, o.nombre_organizacion, p.fecha_inicio, p.fecha_fin, p.descripcion_periodo, " +
                            "m.archivo_foto, m.formato_foto " +
                            "FROM Periodo_Experiencia pe " +
                            "JOIN Periodo p ON pe.id_periodo = p.id_periodo AND pe.correo_persona = p.correo_persona " +
                            "JOIN Organizacion o ON pe.correo_organizacion = o.correo_electronico " +
                            "JOIN Miembro m ON pe.correo_organizacion = m.correo_electronico " +
                            "WHERE pe.correo_persona = ?";
            
            List<Map<String, Object>> experiences = jdbcTemplate.queryForList(sqlExp, email);
            if (!experiences.isEmpty()) {
                processListItems(experiences, "nombre_organizacion", false);
                data.put("experiences", experiences);
            }

            // 4. Education
            String sqlEduSimple = "SELECT pe.nombre_estudio, pe.id_carrera as carrera, p.fecha_inicio, p.fecha_fin, p.descripcion_periodo, " +
                                  "m.archivo_foto, m.formato_foto, o.nombre_organizacion " +
                                  "FROM Periodo_Educativo pe " +
                                  "JOIN Periodo p ON pe.id_periodo = p.id_periodo AND pe.correo_persona = p.correo_persona " +
                                  "LEFT JOIN Carrera c ON pe.id_carrera = c.nombre_carrera " +
                                  "LEFT JOIN Miembro m ON c.correo_electronico = m.correo_electronico " +
                                  "LEFT JOIN Organizacion o ON c.correo_electronico = o.correo_electronico " +
                                  "WHERE pe.correo_persona = ?";
                                  
             List<Map<String, Object>> education = jdbcTemplate.queryForList(sqlEduSimple, email);
             if (!education.isEmpty()) {
                 processListItems(education, "nombre_organizacion", true);
                 data.put("education", education);
             }

            // 5. Interests
            String sqlInterests = "SELECT nombre_interes FROM Expresa WHERE correo_miembro = ?";
            List<String> interests = jdbcTemplate.queryForList(sqlInterests, String.class, email);
            if (!interests.isEmpty()) {
                data.put("interests", interests);
            }
        }

        return generateReportPdf("memberDetail1", data);
    }

    public byte[] generatePeopleFromOrg2(String orgEmail) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("date", LocalDate.now().toString());

        // 1. Fetch Organization Name
        String sqlOrg = "SELECT nombre_organizacion FROM Organizacion WHERE correo_electronico = ?";
        try {
            String orgName = jdbcTemplate.queryForObject(sqlOrg, String.class, orgEmail);
            data.put("orgName", orgName);
        } catch (Exception e) {
             throw new IllegalArgumentException("Organization not found with email: " + orgEmail);
        }

        // 2. Fetch People
        String sqlPeople = "SELECT p.primer_nombre, p.primer_apellido, p.segundo_nombre, p.segundo_apellido, " +
                           "m.correo_electronico, m.encabezado_perfil, m.archivo_foto, m.formato_foto, p.ubicacion_geografica, " +
                           "pe.tipo_cargo " +
                           "FROM Periodo_Experiencia pe " +
                           "JOIN Persona p ON pe.correo_persona = p.correo_electronico " +
                           "JOIN Miembro m ON pe.correo_persona = m.correo_electronico " +
                           "WHERE pe.correo_organizacion = ?";

        List<Map<String, Object>> peopleRaw = jdbcTemplate.queryForList(sqlPeople, orgEmail);
        data.put("totalPeople", peopleRaw.size());

        // Process photos and names common logic
        for (Map<String, Object> person : peopleRaw) {
            // Name Construction
            String p1 = (String) person.get("primer_nombre");
            String p2 = (String) person.get("segundo_nombre");
            String a1 = (String) person.get("primer_apellido");
            String a2 = (String) person.get("segundo_apellido");
            String fullName = p1 + " " + (p2 != null ? p2 + " " : "") + a1 + " " + (a2 != null ? a2 : "");
            person.put("name", fullName);
            
            // Email & Location & Headline
            person.put("email", person.get("correo_electronico"));
            person.put("location", person.get("ubicacion_geografica"));
            person.put("headline", person.get("encabezado_perfil"));

            // Photo/Initials Logic
            byte[] photoBytes = (byte[]) person.get("archivo_foto");
            Object formatObj = person.get("formato_foto");
            String photoFormat = null;
            if (formatObj != null) photoFormat = formatObj.toString();
            
            if (photoBytes != null && photoFormat != null) {
                String base64Photo = java.util.Base64.getEncoder().encodeToString(photoBytes);
                person.put("photoBase64", "data:image/" + photoFormat + ";base64," + base64Photo);
            } else {
                String initials = "";
                if (p1 != null && !p1.isEmpty()) initials += p1.charAt(0);
                if (a1 != null && !a1.isEmpty()) initials += a1.charAt(0);
                person.put("initials", initials.toUpperCase());
            }
        }

        // 3. Group by Type
        Map<String, List<Map<String, Object>>> grouped = new java.util.LinkedHashMap<>();
        
        for (Map<String, Object> person : peopleRaw) {
            String type = (String) person.get("tipo_cargo");
             Object typeObj = person.get("tipo_cargo");
             if (typeObj != null) type = typeObj.toString();
             
            grouped.computeIfAbsent(type, k -> new java.util.ArrayList<>()).add(person);
        }

        List<Map<String, Object>> groupsList = new java.util.ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : grouped.entrySet()) {
            Map<String, Object> groupMap = new HashMap<>();
            groupMap.put("type", entry.getKey());
            groupMap.put("people", entry.getValue());
            groupMap.put("count", entry.getValue().size());
            groupMap.put("plural", entry.getValue().size() != 1);
            groupsList.add(groupMap);
        }
        
        data.put("groups", groupsList);

        return generateReportPdf("peopleFromOrg2", data);
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
    private void processListItems(List<Map<String, Object>> list, String nameKey, boolean isEducation) {
        for (Map<String, Object> item : list) {
            // Photo handling
            byte[] photoBytes = (byte[]) item.get("archivo_foto");
            Object formatObj = item.get("formato_foto");
            String photoFormat = null;
            if (formatObj != null) {
                photoFormat = formatObj.toString();
            }
            
            if (photoBytes != null && photoFormat != null) {
                String base64Photo = java.util.Base64.getEncoder().encodeToString(photoBytes);
                item.put("photoBase64", "data:image/" + photoFormat + ";base64," + base64Photo);
            } else {
                String name = (String) item.get(nameKey);
                if (name != null && !name.isEmpty()) {
                    item.put("initials", name.substring(0, 1).toUpperCase());
                }
            }

            // Date handling
            if (item.get("fecha_inicio") != null) {
                item.put("fecha_inicio", item.get("fecha_inicio").toString());
            }
            if (item.get("fecha_fin") != null) {
                item.put("fecha_fin", item.get("fecha_fin").toString());
            }

            // Education Title Logic
            if (isEducation) {
                String nombreEstudio = (String) item.get("nombre_estudio");
                String carrera = (String) item.get("carrera");
                if (nombreEstudio != null && !nombreEstudio.isEmpty()) {
                    item.put("educationTitle", nombreEstudio);
                } else {
                    item.put("educationTitle", carrera);
                }
            }
        }
    }
}
