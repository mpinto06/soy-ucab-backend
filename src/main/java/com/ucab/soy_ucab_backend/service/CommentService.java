package com.ucab.soy_ucab_backend.service;

import com.ucab.soy_ucab_backend.dto.CommentDto;
import com.ucab.soy_ucab_backend.dto.CommentProjection;
import com.ucab.soy_ucab_backend.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<CommentDto> getComments(String postId, String postAuthorId) {
        List<CommentProjection> projections = commentRepository.findCommentsByPost(postId, postAuthorId);

        return projections.stream().map(p -> {
            String photoBase64 = null;
            if (p.getAutor_foto() != null) {
                photoBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(p.getAutor_foto());
            }

            return new CommentDto(
                    p.getId_comentario(),
                    p.getTexto_comentario(),
                    p.getFecha_hora(),
                    new CommentDto.AuthorDto(
                            p.getCorreo_miembro(),
                            p.getAutor_nombre(),
                            photoBase64,
                            p.getAutor_encabezado()
                    )
            );
        }).toList();
    }
    public void createComment(String userId, com.ucab.soy_ucab_backend.dto.CreateCommentDto dto) {
        com.ucab.soy_ucab_backend.model.Comment comment = new com.ucab.soy_ucab_backend.model.Comment();
        
        java.time.OffsetDateTime now = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC);
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // Use a formatted timestamp for the ID, similar to SQL inserts
        comment.setIdComentario(now.format(formatter));
        comment.setIdPublicacion(dto.getPostId());
        comment.setCorreoAutorPub(dto.getPostAuthorId());
        comment.setCorreoMiembro(userId);
        comment.setTextoComentario(dto.getContent());
        comment.setFechaHora(now);
        
        commentRepository.save(comment);
    }
}
