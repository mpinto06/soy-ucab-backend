package com.ucab.soy_ucab_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MessageService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getConversations(String userEmail) {
        // Get list of unique users interacted with, along with last message info
        // This is a bit complex in SQL. We need the latest message for each pair
        // (userEmail, other)
        String sql = "SELECT DISTINCT ON (other_email) " +
                "   CASE WHEN correo_emisor = ? THEN correo_receptor ELSE correo_emisor END as other_email, " +
                "   m.texto, m.fecha_hora, m.estado_mensaje, m.correo_emisor " +
                "FROM Mensaje m " +
                "WHERE correo_emisor = ? OR correo_receptor = ? " +
                "ORDER BY other_email, m.fecha_hora DESC";

        List<Map<String, Object>> rawConversations = jdbcTemplate.queryForList(sql, userEmail, userEmail, userEmail);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> conv : rawConversations) {
            String otherEmail = (String) conv.get("other_email");

            // Get user details for otherEmail
            // Reuse logic similar to NetworkService or simple query here
            String userSql = "SELECT m.correo_electronico, m.archivo_foto, m.formato_foto, " +
                    "p.primer_nombre, p.primer_apellido, o.nombre_organizacion " +
                    "FROM Miembro m " +
                    "LEFT JOIN Persona p ON m.correo_electronico = p.correo_electronico " +
                    "LEFT JOIN Organizacion o ON m.correo_electronico = o.correo_electronico " +
                    "WHERE m.correo_electronico = ?";

            try {
                Map<String, Object> userInfo = jdbcTemplate.queryForMap(userSql, otherEmail);

                String name;
                String initials = "";
                if (userInfo.get("primer_nombre") != null) {
                    name = userInfo.get("primer_nombre") + " " + userInfo.get("primer_apellido");
                    initials = ((String) userInfo.get("primer_nombre")).substring(0, 1) +
                            ((String) userInfo.get("primer_apellido")).substring(0, 1);
                } else if (userInfo.get("nombre_organizacion") != null) {
                    name = (String) userInfo.get("nombre_organizacion");
                    initials = name.substring(0, Math.min(2, name.length()));
                } else {
                    name = otherEmail;
                    initials = "U";
                }

                // Count unread
                String unreadSql = "SELECT COUNT(*) FROM Mensaje WHERE correo_emisor = ? AND correo_receptor = ? AND estado_mensaje != 'leido'";
                Integer unread = jdbcTemplate.queryForObject(unreadSql, Integer.class, otherEmail, userEmail);

                Map<String, Object> dto = new HashMap<>();
                dto.put("email", otherEmail);
                dto.put("name", name);
                dto.put("initials", initials.toUpperCase());

                if (userInfo.get("archivo_foto") != null) {
                    String base64 = Base64.getEncoder().encodeToString((byte[]) userInfo.get("archivo_foto"));
                    dto.put("photo", "data:image/" + userInfo.get("formato_foto") + ";base64," + base64);
                } else {
                    dto.put("photo", null);
                }

                dto.put("lastMessage", conv.get("texto")); // Could be null if only files?
                dto.put("lastMessageTime", conv.get("fecha_hora"));
                dto.put("unread", unread);

                result.add(dto);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Sort by date DESC
        result.sort((a, b) -> ((java.sql.Timestamp) b.get("lastMessageTime"))
                .compareTo((java.sql.Timestamp) a.get("lastMessageTime")));

        return result;
    }

    public List<Map<String, Object>> getChatHistory(String userEmail, String otherEmail) {
        String sql = "SELECT * FROM Mensaje " +
                "WHERE (correo_emisor = ? AND correo_receptor = ?) " +
                "OR (correo_emisor = ? AND correo_receptor = ?) " +
                "ORDER BY fecha_hora ASC";

        List<Map<String, Object>> messages = jdbcTemplate.queryForList(sql, userEmail, otherEmail, otherEmail,
                userEmail);

        // Mark as read if receiving
        String markReadSql = "UPDATE Mensaje SET estado_mensaje = 'leido' WHERE correo_emisor = ? AND correo_receptor = ? AND estado_mensaje != 'leido'";
        jdbcTemplate.update(markReadSql, otherEmail, userEmail);

        for (Map<String, Object> msg : messages) {
            String id = (String) msg.get("id_mensaje");

            // Get files
            String filesSql = "SELECT nombre_archivo, formato, octet_length(archivo) as size FROM Archivo_Mensaje WHERE id_mensaje = ?";
            List<Map<String, Object>> files = jdbcTemplate.queryForList(filesSql, id);

            // Add file structure expected by frontend (no bytes here to keep it light, need
            // download endpoint or base64 if small)
            // Ideally we'd have a download URL. For now, let's skip returning heavy blobs
            // in list.

            List<Map<String, Object>> fileDtos = new ArrayList<>();
            for (Map<String, Object> f : files) {
                Map<String, Object> fd = new HashMap<>();
                fd.put("name", f.get("nombre_archivo"));
                fd.put("type", f.get("formato")); // This is enum name, frontend expects mime type approx
                fd.put("size", f.get("size"));
                // We need a way to serve the file. Maybe base64 if small, or an endpoint.
                // Let's assume we'll serve it via a separate endpoint
                // /messages/file/{id}/{filename}
                // For now, let's put a placeholder URL
                fd.put("url", "");
                fileDtos.add(fd);
            }
            msg.put("files", fileDtos);
        }

        return messages;
    }

    @Transactional
    public void sendMessage(String sender, String receiver, String content, List<MultipartFile> files) {
        // Use timestamp as ID as requested
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        // Format ID to be human readable: YYYY-MM-DD HH:MM:SS.SSS
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String idInfo = sdf.format(now);

        String sql = "INSERT INTO Mensaje (id_mensaje, correo_emisor, correo_receptor, texto, fecha_hora, estado_mensaje) "
                +
                "VALUES (?, ?, ?, ?, ?, 'recibido')";

        jdbcTemplate.update(sql, idInfo, sender, receiver, content, now);

        if (files != null) {
            String fileSql = "INSERT INTO Archivo_Mensaje (id_mensaje, correo_emisor, correo_receptor, nombre_archivo, archivo, formato) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?::extension_multimedia)";

            for (MultipartFile file : files) {
                try {
                    String originalName = file.getOriginalFilename();
                    String ext = "";
                    if (originalName != null && originalName.contains(".")) {
                        ext = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
                    }
                    // Map to enum simple check
                    if (!Arrays.asList("mp4", "jpeg", "jpg", "png", "pdf").contains(ext)) {
                        // Default fallback or skip
                        continue;
                    }

                    jdbcTemplate.update(fileSql, idInfo, sender, receiver, originalName, file.getBytes(), ext);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<String, Object> getFile(String messageId, String filename) {
        String sql = "SELECT nombre_archivo, archivo, formato FROM Archivo_Mensaje WHERE id_mensaje = ? AND nombre_archivo = ?";
        try {
            return jdbcTemplate.queryForMap(sql, messageId, filename);
        } catch (Exception e) {
            return null;
        }
    }
}
