package com.ucab.soy_ucab_backend.dto;

import java.time.Instant;

public interface FeedPostProjection {
    String getId_pub();
    String getAutor_id(); // previously getAutor
    String getAutor_nombre();
    byte[] getAutor_foto();
    String getAutor_encabezado();
    String getGrupo();
    String getTexto();
    Instant getFecha();
    Integer getLikes();
    Integer getComentarios();
    Boolean getMi_like();
    String getIntereses();
    Long getTotal_registros();
}
