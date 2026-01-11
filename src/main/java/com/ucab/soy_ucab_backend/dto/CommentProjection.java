package com.ucab.soy_ucab_backend.dto;

import java.time.Instant;

public interface CommentProjection {
    String getId_comentario();
    String getId_publicacion();
    String getCorreo_autor_pub();
    String getCorreo_miembro();
    String getTexto_comentario();
    Instant getFecha_hora();
    
    // Author details
    String getAutor_nombre();
    byte[] getAutor_foto();
    String getAutor_encabezado();
}
