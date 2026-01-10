package com.ucab.soy_ucab_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class JobService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getAllOffers(String email) {
        String sql = """
                SELECT o.nombre_organizacion as empresa,
                       ot.nombre_cargo as cargo,
                       CAST(ot.tipo_cargo AS text) as tipo_cargo,
                       CAST(ot.modalidad AS text) as modalidad,
                       ot.ubicacion,
                       ot.descripcion_cargo,
                       ot.correo_publicador as publicador_email,
                       CAST(ot.estado_oferta AS text) as estado,
                       TO_CHAR(ot.fecha_publicacion, 'DD Mon YYYY') as fecha_publicacion,
                       m.archivo_foto,
                       m.formato_foto,
                       CASE WHEN g.nombre_cargo IS NOT NULL THEN true ELSE false END as guardada,
                       CASE WHEN a.nombre_cargo IS NOT NULL THEN true ELSE false END as aplicada
                FROM Oferta_Trabajo ot
                JOIN Organizacion o ON ot.correo_publicador = o.correo_electronico
                LEFT JOIN Miembro m ON o.correo_electronico = m.correo_electronico
                LEFT JOIN Guarda g ON g.correo_publicador = ot.correo_publicador
                                   AND g.nombre_cargo = ot.nombre_cargo
                                   AND g.correo_persona = ?
                LEFT JOIN Aplica a ON a.correo_publicador = ot.correo_publicador
                                   AND a.nombre_cargo = ot.nombre_cargo
                                   AND a.correo_aplicante = ?
                WHERE ot.estado_oferta = 'abierta'
                """;

        List<Map<String, Object>> offers = jdbcTemplate.queryForList(sql, email, email);
        processOffers(offers);
        return offers;
    }

    public void toggleSaveJob(String userEmail, String publisherEmail, String jobTitle) {
        // Check if exists
        String checkSql = "SELECT COUNT(*) FROM Guarda WHERE correo_persona = ? AND correo_publicador = ? AND nombre_cargo = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userEmail, publisherEmail, jobTitle);

        if (count != null && count > 0) {
            // Delete
            String deleteSql = "DELETE FROM Guarda WHERE correo_persona = ? AND correo_publicador = ? AND nombre_cargo = ?";
            jdbcTemplate.update(deleteSql, userEmail, publisherEmail, jobTitle);
        } else {
            // Insert
            String insertSql = "INSERT INTO Guarda (correo_persona, correo_publicador, nombre_cargo) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertSql, userEmail, publisherEmail, jobTitle);
        }
    }

    public void applyToJob(String userEmail, String publisherEmail, String jobTitle, String description,
            org.springframework.web.multipart.MultipartFile cvFile) throws java.io.IOException {
        byte[] fileBytes = null;
        String fileName = null;

        if (cvFile != null && !cvFile.isEmpty()) {
            fileBytes = cvFile.getBytes();
            fileName = cvFile.getOriginalFilename();
        }

        // Procedure signature:
        // p_correo_aplicante, p_correo_publicador, p_nombre_cargo,
        // p_nombre_archivo, p_archivo_cv, p_texto_aplicante
        String sql = "CALL aplicar_a_oferta(?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, userEmail, publisherEmail, jobTitle, fileName, fileBytes, description);
    }

    public void cancelApplication(String userEmail, String publisherEmail, String jobTitle) {
        String deleteSql = "DELETE FROM Aplica WHERE correo_aplicante = ? AND correo_publicador = ? AND nombre_cargo = ?";
        jdbcTemplate.update(deleteSql, userEmail, publisherEmail, jobTitle);
    }

    public List<Map<String, Object>> getOrganizationOffers(String email) {
        String sql = """
                SELECT ot.nombre_cargo as cargo,
                       CAST(ot.tipo_cargo AS text) as tipo_cargo,
                       CAST(ot.modalidad AS text) as modalidad,
                       ot.ubicacion,
                       ot.descripcion_cargo,
                       ot.correo_publicador as publicador_email,
                       CAST(ot.estado_oferta AS text) as estado,
                       TO_CHAR(ot.fecha_publicacion, 'DD Mon YYYY') as fecha_publicacion,
                       (SELECT COUNT(*) FROM Aplica a
                        WHERE a.correo_publicador = ot.correo_publicador
                        AND a.nombre_cargo = ot.nombre_cargo) as aplicantes
                FROM Oferta_Trabajo ot
                WHERE LOWER(ot.correo_publicador) = LOWER(?)
                ORDER BY ot.fecha_publicacion DESC
                """;

        List<Map<String, Object>> offers = jdbcTemplate.queryForList(sql, email);

        for (Map<String, Object> offer : offers) {
            if (offer.containsKey("estado")) {
                String estadoOferta = (String) offer.get("estado");
                if ("abierta".equalsIgnoreCase(estadoOferta)) {
                    offer.put("estado_badge", "Abierta");
                    offer.put("estado_class", "badge-process"); // Green/Blue
                } else {
                    offer.put("estado_badge", "Cerrada");
                    offer.put("estado_class", "badge-closed"); // Red/Gray
                }
            }
        }
        return offers;
    }

    public void createJobOffer(String publisherEmail, String title, String description,
            String type, String modality, String location) {
        // Use subquery to ensure we get the correct casing of the email for the FK
        // constraint
        String sql = """
                    INSERT INTO Oferta_Trabajo (correo_publicador, nombre_cargo, descripcion_cargo, tipo_cargo, modalidad, ubicacion, estado_oferta)
                    VALUES (
                        (SELECT correo_electronico FROM Organizacion WHERE LOWER(correo_electronico) = LOWER(?)),
                        ?, ?, CAST(? AS tipo_cargo_exp), CAST(? AS modalidad_trabajo), ?, 'abierta'
                    )
                """;
        // Note: The order of parameters must matches the placeholders.
        // 1. publisherEmail (for subquery)
        // 2. title
        // 3. description
        // 4. type
        // 5. modality
        // 6. location
        jdbcTemplate.update(sql, publisherEmail, title, description, type, modality, location);
    }

    public boolean isOrganization(String email) {
        System.out.println("Checking role for email: " + email);
        String sql = "SELECT COUNT(*) FROM Organizacion WHERE LOWER(correo_electronico) = LOWER(?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        System.out.println("Found count: " + count);
        return count != null && count > 0;
    }

    public List<Map<String, Object>> getJobApplicants(String publisherEmail, String jobTitle) {
        String sql = """
                SELECT p.primer_nombre || ' ' || p.primer_apellido as nombre,
                       p.correo_electronico,
                       a.archivo_cv,
                       a.nombre_archivo,
                       TO_CHAR(a.fecha_aplicacion, 'DD Mon YYYY') as fecha_aplicacion,
                       a.texto_aplicante as descripcion,
                       m.archivo_foto,
                       m.formato_foto
                FROM Aplica a
                JOIN Persona p ON a.correo_aplicante = p.correo_electronico
                LEFT JOIN Miembro m ON p.correo_electronico = m.correo_electronico
                WHERE LOWER(a.correo_publicador) = LOWER(?)
                AND a.nombre_cargo = ?
                ORDER BY a.fecha_aplicacion DESC
                """;

        List<Map<String, Object>> applicants = jdbcTemplate.queryForList(sql, publisherEmail, jobTitle);
        System.out.println("DEBUG: Found " + applicants.size() + " applicants.");

        for (Map<String, Object> app : applicants) {
            // Process Image
            byte[] photoBytes = (byte[]) app.get("archivo_foto");
            if (photoBytes != null && photoBytes.length > 0) {
                String format = (String) app.get("formato_foto");
                if (format == null)
                    format = "png";
                String base64 = java.util.Base64.getEncoder().encodeToString(photoBytes);
                app.put("foto_base64", "data:image/" + format + ";base64," + base64);
            } else {
                app.put("foto_base64", null);
            }

            // Process CV for download (optional, or just send bytes if needed, but usually
            // frontend needs ID or link)
            // For now we send metadata. If we want to download, we might need a separate
            // endpoint or send Base64 if small.
            // Let's send Base64 for CV if it exists and is not too huge, or just name.
            // Given the context, let's keep it simple: just metadata. Actual download might
            // require another endpoint
            // or we include base64 if user asks. The user wanted "applicants", usually to
            // see them.
            // If we want to download the CV, we can do it via a separate endpoint
            // /jobs/download-cv? ...
            // BUT, for now let's just return the byte array? NO, that's heavy for a list.
            // Let's return a flag indicating it exists.
            if (app.get("archivo_cv") != null) {
                app.put("has_cv", true);
                // We don't send the blob in the list to be light.
                app.remove("archivo_cv");
            } else {
                app.put("has_cv", false);
            }
        }
        return applicants;
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

            // Random logo color or placeholder if needed
            offer.put("logo_bg", "bg-blue-100"); // Example

            // Process Image
            byte[] photoBytes = (byte[]) offer.get("archivo_foto");
            if (photoBytes != null && photoBytes.length > 0) {
                String format = (String) offer.get("formato_foto");
                if (format == null)
                    format = "png"; // fallback
                String base64 = java.util.Base64.getEncoder().encodeToString(photoBytes);
                offer.put("logo_base64", "data:image/" + format + ";base64," + base64);
            } else {
                offer.put("logo_base64", null);
            }

            if (offer.containsKey("estado")) {
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
}
