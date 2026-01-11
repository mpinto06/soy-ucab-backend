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
}
