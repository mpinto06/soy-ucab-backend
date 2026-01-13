package com.ucab.soy_ucab_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NetworkService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @jakarta.annotation.PostConstruct
    public void init() {
        try {
            // Eliminar la restricción que impide el orden correcto (solicitante,
            // solicitado)
            jdbcTemplate.execute("ALTER TABLE Es_Amigo DROP CONSTRAINT IF EXISTS chk_orden_correos");
            System.out.println("Constraint chk_orden_correos dropped successfully.");
        } catch (Exception e) {
            System.out.println("Warning: Could not drop constraint chk_orden_correos: " + e.getMessage());
        }
    }

    public Map<String, Object> getNetworkData(String email) {
        Map<String, Object> data = new HashMap<>();
        data.put("date", LocalDate.now().toString());

        String memberName = "";
        boolean isOrg = false;

        // Se verifica si es una organizacion o una persona
        try {
            String sqlOrgData = "SELECT o.nombre_organizacion, m.archivo_foto, m.formato_foto " +
                    "FROM Organizacion o JOIN Miembro m ON o.correo_electronico = m.correo_electronico " +
                    "WHERE o.correo_electronico = ?";
            Map<String, Object> orgMap = jdbcTemplate.queryForMap(sqlOrgData, email);
            memberName = (String) orgMap.get("nombre_organizacion");
            isOrg = true;

            if (memberName != null && !memberName.isEmpty()) {
                String[] words = memberName.split("\\s+");
                if (words.length > 1) {
                    data.put("memberInitials", (words[0].substring(0, 1) + words[1].substring(0, 1)).toUpperCase());
                } else {
                    data.put("memberInitials", memberName.substring(0, 1).toUpperCase());
                }
            } else {
                data.put("memberInitials", "?");
            }

            byte[] photoBytes = (byte[]) orgMap.get("archivo_foto");
            String photoFormat = (String) orgMap.get("formato_foto");
            if (photoBytes != null && photoFormat != null) {
                String base64Photo = java.util.Base64.getEncoder().encodeToString(photoBytes);
                data.put("memberPhoto", "data:image/" + photoFormat + ";base64," + base64Photo);
            }

        } catch (Exception e) {
            // Es una persona
            String sqlPersonData = "SELECT p.primer_nombre, p.primer_apellido, m.archivo_foto, m.formato_foto " +
                    "FROM Persona p JOIN Miembro m ON p.correo_electronico = m.correo_electronico " +
                    "WHERE p.correo_electronico = ?";
            try {
                Map<String, Object> pMap = jdbcTemplate.queryForMap(sqlPersonData, email);
                String p1 = (String) pMap.get("primer_nombre");
                String a1 = (String) pMap.get("primer_apellido");
                memberName = p1 + " " + a1;

                String initials = "";
                if (p1 != null && !p1.isEmpty())
                    initials += p1.charAt(0);
                if (a1 != null && !a1.isEmpty())
                    initials += a1.charAt(0);
                data.put("memberInitials", initials.toUpperCase());

                byte[] photoBytes = (byte[]) pMap.get("archivo_foto");
                String photoFormat = (String) pMap.get("formato_foto");
                if (photoBytes != null && photoFormat != null) {
                    String base64Photo = java.util.Base64.getEncoder().encodeToString(photoBytes);
                    data.put("memberPhoto", "data:image/" + photoFormat + ";base64," + base64Photo);
                }

            } catch (Exception ex) {
                memberName = "Usuario";
                data.put("memberInitials", "?");
            }
        }
        data.put("memberName", memberName);

        // Seguidores Comunes
        String sqlFollowers = "SELECT m.correo_electronico, m.encabezado_perfil, m.archivo_foto, m.formato_foto, " +
                "p.primer_nombre, p.segundo_nombre, p.primer_apellido, p.segundo_apellido, p.ubicacion_geografica, " +
                "o.nombre_organizacion " +
                "FROM Sigue s " +
                "JOIN Miembro m ON s.correo_seguidor = m.correo_electronico " +
                "LEFT JOIN Persona p ON m.correo_electronico = p.correo_electronico " +
                "LEFT JOIN Organizacion o ON m.correo_electronico = o.correo_electronico " +
                "WHERE s.correo_seguido = ?";
        List<Map<String, Object>> followers = jdbcTemplate.queryForList(sqlFollowers, email);
        enrichMembersWithDetails(followers, email);
        data.put("followers", followers);

        if (!isOrg) {
            // Amigos
            String sqlFriends = "SELECT m.correo_electronico, m.encabezado_perfil, m.archivo_foto, m.formato_foto, " +
                    "p.primer_nombre, p.segundo_nombre, p.primer_apellido, p.segundo_apellido, p.ubicacion_geografica, "
                    +
                    "o.nombre_organizacion " +
                    "FROM Es_Amigo ea " +
                    "JOIN Miembro m ON (CASE WHEN ea.correo_persona1 = ? THEN ea.correo_persona2 ELSE ea.correo_persona1 END) = m.correo_electronico "
                    +
                    "LEFT JOIN Persona p ON m.correo_electronico = p.correo_electronico " +
                    "LEFT JOIN Organizacion o ON m.correo_electronico = o.correo_electronico " +
                    "WHERE (ea.correo_persona1 = ? OR ea.correo_persona2 = ?) AND ea.estado = 'aceptada'";
            List<Map<String, Object>> friends = jdbcTemplate.queryForList(sqlFriends, email, email, email);
            enrichMembersWithDetails(friends, email);
            data.put("friends", friends);

            // Mark followers as friends if applicable
            java.util.Set<String> friendEmails = new java.util.HashSet<>();
            for (Map<String, Object> friend : friends) {
                friendEmails.add((String) friend.get("correo_electronico"));
            }
            for (Map<String, Object> follower : followers) {
                String followerEmail = (String) follower.get("correo_electronico");
                if (friendEmails.contains(followerEmail)) {
                    follower.put("isFriend", true);
                }

                // Check if there is a pending request sent by the current user to this follower
                String sqlCheckPending = "SELECT COUNT(*) FROM Es_Amigo WHERE correo_persona1 = ? AND correo_persona2 = ? AND estado = 'pendiente'";
                Integer pendingCount = jdbcTemplate.queryForObject(sqlCheckPending, Integer.class, email,
                        followerEmail);
                if (pendingCount != null && pendingCount > 0) {
                    follower.put("requestSent", true);
                }
            }

            // Siguiendo (Following) - People the user follows
            String sqlFollowing = "SELECT m.correo_electronico, m.encabezado_perfil, m.archivo_foto, m.formato_foto, " +
                    "p.primer_nombre, p.segundo_nombre, p.primer_apellido, p.segundo_apellido, p.ubicacion_geografica, "
                    +
                    "o.nombre_organizacion " +
                    "FROM Sigue s " +
                    "JOIN Miembro m ON s.correo_seguido = m.correo_electronico " +
                    "LEFT JOIN Persona p ON m.correo_electronico = p.correo_electronico " +
                    "LEFT JOIN Organizacion o ON m.correo_electronico = o.correo_electronico " +
                    "WHERE s.correo_seguidor = ?";
            List<Map<String, Object>> following = jdbcTemplate.queryForList(sqlFollowing, email);
            enrichMembersWithDetails(following, email);

            // Mark following as friends if applicable
            for (Map<String, Object> f : following) {
                String fEmail = (String) f.get("correo_electronico");
                if (friendEmails.contains(fEmail)) {
                    f.put("isFriend", true);
                }
            }
            data.put("following", following);

            // Invitaciones (Solicitudes Pendientes recibidas)
            String sqlInvitations = "SELECT m.correo_electronico, m.encabezado_perfil, m.archivo_foto, m.formato_foto, "
                    +
                    "p.primer_nombre, p.segundo_nombre, p.primer_apellido, p.segundo_apellido, p.ubicacion_geografica, "
                    +
                    "o.nombre_organizacion " +
                    "FROM Es_Amigo ea " +
                    "JOIN Miembro m ON ea.correo_persona1 = m.correo_electronico " +
                    "LEFT JOIN Persona p ON m.correo_electronico = p.correo_electronico " +
                    "LEFT JOIN Organizacion o ON m.correo_electronico = o.correo_electronico " +
                    "WHERE ea.correo_persona2 = ? AND ea.estado = 'pendiente'";
            List<Map<String, Object>> invitations = jdbcTemplate.queryForList(sqlInvitations, email);
            enrichMembersWithDetails(invitations, email);
            data.put("invitations", invitations);

            // Sugerencias (Combinación de Colegas y Compañeros)
            List<Map<String, Object>> suggestions = new java.util.ArrayList<>();

            // 1. Colegas
            String sqlColleagues = "SELECT DISTINCT m.correo_electronico, m.encabezado_perfil, m.archivo_foto, m.formato_foto, "
                    +
                    "p.primer_nombre, p.segundo_nombre, p.primer_apellido, p.segundo_apellido, p.ubicacion_geografica, "
                    +
                    "o.nombre_organizacion " +
                    "FROM Periodo_Experiencia pe1 " +
                    "JOIN Periodo_Experiencia pe2 ON pe1.correo_organizacion = pe2.correo_organizacion " +
                    "JOIN Miembro m ON pe2.correo_persona = m.correo_electronico " +
                    "LEFT JOIN Persona p ON m.correo_electronico = p.correo_electronico " +
                    "LEFT JOIN Organizacion o ON m.correo_electronico = o.correo_electronico " +
                    "WHERE pe1.correo_persona = ? AND pe2.correo_persona != ?";
            List<Map<String, Object>> colleagues = jdbcTemplate.queryForList(sqlColleagues, email, email);

            // 2. Compañeros
            String sqlClassmates = "SELECT DISTINCT m.correo_electronico, m.encabezado_perfil, m.archivo_foto, m.formato_foto, "
                    +
                    "p.primer_nombre, p.segundo_nombre, p.primer_apellido, p.segundo_apellido, p.ubicacion_geografica, "
                    +
                    "o.nombre_organizacion " +
                    "FROM Periodo_Educativo pe1 " +
                    "JOIN Carrera c1 ON pe1.id_carrera = c1.nombre_carrera " +
                    "JOIN Carrera c2 ON c1.correo_electronico = c2.correo_electronico " +
                    "JOIN Periodo_Educativo pe2 ON pe2.id_carrera = c2.nombre_carrera " +
                    "JOIN Miembro m ON pe2.correo_persona = m.correo_electronico " +
                    "LEFT JOIN Persona p ON m.correo_electronico = p.correo_electronico " +
                    "LEFT JOIN Organizacion o ON m.correo_electronico = o.correo_electronico " +
                    "WHERE pe1.correo_persona = ? AND pe2.correo_persona != ?";
            List<Map<String, Object>> classmates = jdbcTemplate.queryForList(sqlClassmates, email, email);

            // Merge and deduplicate
            java.util.Set<String> addedEmails = new java.util.HashSet<>();

            // Exclude self, friends, following, and pending requests from suggestions
            addedEmails.add(email);
            for (Map<String, Object> f : friends) {
                addedEmails.add((String) f.get("correo_electronico"));
            }
            for (Map<String, Object> f : following) {
                addedEmails.add((String) f.get("correo_electronico"));
            }

            // Also exclude pending requests (sent by me)
            String sqlPendingSent = "SELECT correo_persona2 FROM Es_Amigo WHERE correo_persona1 = ? AND estado = 'pendiente'";
            List<String> pendingSent = jdbcTemplate.queryForList(sqlPendingSent, String.class, email);
            addedEmails.addAll(pendingSent);

            // Also exclude pending requests (received by me) - already in invitations but
            // good to be safe
            String sqlPendingReceived = "SELECT correo_persona1 FROM Es_Amigo WHERE correo_persona2 = ? AND estado = 'pendiente'";
            List<String> pendingReceived = jdbcTemplate.queryForList(sqlPendingReceived, String.class, email);
            addedEmails.addAll(pendingReceived);

            for (Map<String, Object> c : colleagues) {
                String e = (String) c.get("correo_electronico");
                if (!addedEmails.contains(e)) {
                    suggestions.add(c);
                    addedEmails.add(e);
                }
            }
            for (Map<String, Object> c : classmates) {
                String e = (String) c.get("correo_electronico");
                if (!addedEmails.contains(e)) {
                    suggestions.add(c);
                    addedEmails.add(e);
                }
            }

            // 3. Organizations and Associated Organizations
            String sqlOrgs = "SELECT m.correo_electronico, m.encabezado_perfil, m.archivo_foto, m.formato_foto, " +
                    "o.nombre_organizacion, true as \"isOrg\" " +
                    "FROM Organizacion o " +
                    "JOIN Miembro m ON o.correo_electronico = m.correo_electronico " +
                    "LEFT JOIN Organizacion_Asociada oa ON o.correo_electronico = oa.correo_electronico";
            // We fetch all and filter in memory or we could filter by NOT EXISTS in Sigue
            // table.
            // Given the logic above uses a Set to dedup, we stick to that pattern.
            List<Map<String, Object>> orgs = jdbcTemplate.queryForList(sqlOrgs);

            for (Map<String, Object> o : orgs) {
                String e = (String) o.get("correo_electronico");
                if (!addedEmails.contains(e)) {
                    suggestions.add(o);
                    addedEmails.add(e);
                }
            }

            enrichMembersWithDetails(suggestions, email);
            data.put("suggestions", suggestions);
        }

        return data;

    }

    private void enrichMembersWithDetails(List<Map<String, Object>> list, String perspectiveUserEmail) {
        for (Map<String, Object> member : list) {
            // Name
            String p1 = (String) member.get("primer_nombre");
            String p2 = (String) member.get("segundo_nombre");
            String a1 = (String) member.get("primer_apellido");
            String a2 = (String) member.get("segundo_apellido");
            String orgName = (String) member.get("nombre_organizacion");

            String fullName;
            String initials = "";

            if (p1 != null) { // Es persona
                fullName = p1 + " " + (p2 != null ? p2 + " " : "") + a1 + " " + (a2 != null ? a2 : "");
                if (!p1.isEmpty())
                    initials += p1.charAt(0);
                if (a1 != null && !a1.isEmpty())
                    initials += a1.charAt(0);
                member.put("isOrg", false);
            } else if (orgName != null) { // Es organizacion
                fullName = orgName;
                if (!orgName.isEmpty())
                    initials += orgName.substring(0, 1);
                member.put("isOrg", true);
            } else {
                fullName = "Usuario";
                member.put("isOrg", false);
            }
            member.put("name", fullName);
            member.put("initials", initials.toUpperCase());
            member.put("headline", member.get("encabezado_perfil"));
            member.put("location", member.get("ubicacion_geografica"));

            // Foto
            byte[] photoBytes = (byte[]) member.get("archivo_foto");
            Object formatObj = member.get("formato_foto");
            if (photoBytes != null && formatObj != null) {
                String base64Photo = java.util.Base64.getEncoder().encodeToString(photoBytes);
                member.put("photoBase64", "data:image/" + formatObj.toString() + ";base64," + base64Photo);
            }

            // Cantidad de Amigos Mutuos
            String targetEmail = (String) member.get("correo_electronico");
            if (targetEmail != null) {
                member.put("email", targetEmail); // Map to frontend expected key
                member.put("mutualFriendsCount", 0);
                int mutual = getMutualFriendsCount(perspectiveUserEmail, targetEmail);
                if (mutual > 0)
                    member.put("mutualFriendsCount", mutual);

                // Trabajo actual
                String sqlJob = "SELECT pe.cargo, o.nombre_organizacion " +
                        "FROM Periodo_Experiencia pe " +
                        "JOIN Periodo p ON pe.id_periodo = p.id_periodo AND pe.correo_persona = p.correo_persona " +
                        "JOIN Organizacion o ON pe.correo_organizacion = o.correo_electronico " +
                        "WHERE pe.correo_persona = ? AND p.fecha_fin IS NULL " +
                        "ORDER BY p.fecha_inicio DESC LIMIT 1";
                try {
                    Map<String, Object> job = jdbcTemplate.queryForMap(sqlJob, targetEmail);
                    member.put("currentJob", job.get("cargo") + " en " + job.get("nombre_organizacion"));
                } catch (Exception e) {
                }
            }
        }
    }

    private int getMutualFriendsCount(String email1, String email2) {
        String sql = "SELECT COUNT(*) FROM " +
                "(SELECT CASE WHEN correo_persona1 = ? THEN correo_persona2 ELSE correo_persona1 END as friend " +
                " FROM Es_Amigo WHERE (correo_persona1 = ? OR correo_persona2 = ?) AND estado = 'aceptada') as f1 " +
                "JOIN " +
                "(SELECT CASE WHEN correo_persona1 = ? THEN correo_persona2 ELSE correo_persona1 END as friend " +
                " FROM Es_Amigo WHERE (correo_persona1 = ? OR correo_persona2 = ?) AND estado = 'aceptada') as f2 " +
                "ON f1.friend = f2.friend";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, email1, email1, email1, email2, email2, email2);
        } catch (Exception e) {
            return 0;
        }
    }

    public String manageFriendRequest(String userEmail, String targetEmail, String action, boolean force) {
        System.out.println("Service manageFriendRequest: " + userEmail + " -> " + targetEmail + " Action: " + action
                + " Force: " + force);
        if ("accept".equals(action)) {
            String sql = "UPDATE Es_Amigo SET estado = 'aceptada' WHERE correo_persona2 = ? AND correo_persona1 = ? AND estado = 'pendiente'";
            int rows = jdbcTemplate.update(sql, userEmail, targetEmail);
            System.out.println("Rows updated (accept): " + rows);
            return "SUCCESS";
        } else if ("reject".equals(action)) {
            String sql = "UPDATE Es_Amigo SET estado = 'rechazada' WHERE correo_persona2 = ? AND correo_persona1 = ? AND estado = 'pendiente'";
            int rows = jdbcTemplate.update(sql, userEmail, targetEmail);
            System.out.println("Rows updated (reject): " + rows);
            return "SUCCESS";
        } else if ("request".equals(action)) {
            // Check current status and direction
            // We need to know WHO initiated the previous request to decide if we swap
            String checkSql = "SELECT correo_persona1, estado FROM Es_Amigo WHERE (correo_persona1 = ? AND correo_persona2 = ?) OR (correo_persona1 = ? AND correo_persona2 = ?)";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(checkSql, userEmail, targetEmail, targetEmail,
                    userEmail);

            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.get(0);
                String currentStatus = (String) row.get("estado");
                String previousRequester = (String) row.get("correo_persona1");

                if ("rechazada".equals(currentStatus)) {
                    if (force) {
                        if (previousRequester.equals(userEmail)) {
                            // Same requester, just reset to pending
                            String updateSql = "UPDATE Es_Amigo SET estado = 'pendiente', fecha_solicitud = CURRENT_DATE WHERE correo_persona1 = ? AND correo_persona2 = ?";
                            jdbcTemplate.update(updateSql, userEmail, targetEmail);
                        } else {
                            // The person who rejected is now requesting.
                            // SWAP the columns so they become the new requester (persona1).
                            // We update the row where persona1 was the targetEmail and persona2 was
                            // userEmail.
                            String updateSql = "UPDATE Es_Amigo SET correo_persona1 = ?, correo_persona2 = ?, estado = 'pendiente', fecha_solicitud = CURRENT_DATE WHERE correo_persona1 = ? AND correo_persona2 = ?";
                            jdbcTemplate.update(updateSql, userEmail, targetEmail, targetEmail, userEmail);
                        }
                        return "SUCCESS";
                    } else {
                        return "REJECTED";
                    }
                }
                return "EXISTS";
            }

            // Brute force: Ensure constraint is gone before inserting
            try {
                jdbcTemplate.execute("ALTER TABLE Es_Amigo DROP CONSTRAINT IF EXISTS chk_orden_correos");
            } catch (Exception e) {
            }

            String sqlInsert = "INSERT INTO Es_Amigo (correo_persona1, correo_persona2, estado) VALUES (?, ?, 'pendiente')";
            jdbcTemplate.update(sqlInsert, userEmail, targetEmail);
            return "SUCCESS";
        }
        return "UNKNOWN_ACTION";
    }

    public void manageFollow(String followerEmail, String followedEmail, String action) {
        System.out.println("Service manageFollow: " + followerEmail + " -> " + followedEmail + " Action: " + action);
        if ("follow".equals(action)) {
            // Explicitly specify conflict target
            String sql = "INSERT INTO Sigue (correo_seguidor, correo_seguido, fecha_hora) VALUES (?, ?, CURRENT_DATE) ON CONFLICT (correo_seguidor, correo_seguido) DO NOTHING";
            // Propagate exception
            jdbcTemplate.update(sql, followerEmail, followedEmail);
            System.out.println("Inserted follow");
        } else if ("unfollow".equals(action)) {
            String sql = "DELETE FROM Sigue WHERE correo_seguidor = ? AND correo_seguido = ?";
            int rows = jdbcTemplate.update(sql, followerEmail, followedEmail);
            System.out.println("Rows deleted (unfollow): " + rows);
        }
    }
}
