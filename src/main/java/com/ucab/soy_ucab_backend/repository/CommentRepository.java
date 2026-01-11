package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.dto.CommentProjection;
import com.ucab.soy_ucab_backend.model.Comment;
import com.ucab.soy_ucab_backend.model.CommentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, CommentId> {

    @Query(value = """
        SELECT c.*,
               COALESCE(p.primer_nombre || ' ' || p.primer_apellido, o.nombre_organizacion, m.correo_electronico) as autor_nombre,
               m.archivo_foto,
               m.encabezado_perfil
        FROM Comenta c
        LEFT JOIN Miembro m ON c.correo_miembro = m.correo_electronico
        LEFT JOIN Persona p ON c.correo_miembro = p.correo_electronico
        LEFT JOIN Organizacion o ON c.correo_miembro = o.correo_electronico
        WHERE c.id_publicacion = :postId 
          AND c.correo_autor_pub = :postAuthorId
        ORDER BY c.fecha_hora ASC
    """, nativeQuery = true)
    List<CommentProjection> findCommentsByPost(@Param("postId") String postId, @Param("postAuthorId") String postAuthorId);
}
