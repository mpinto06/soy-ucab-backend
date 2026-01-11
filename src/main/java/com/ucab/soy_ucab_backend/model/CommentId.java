package com.ucab.soy_ucab_backend.model;

import java.io.Serializable;
import java.util.Objects;

public class CommentId implements Serializable {
    private String idComentario;
    private String idPublicacion;
    private String correoAutorPub;
    private String correoMiembro;

    public CommentId() {}

    public CommentId(String idComentario, String idPublicacion, String correoAutorPub, String correoMiembro) {
        this.idComentario = idComentario;
        this.idPublicacion = idPublicacion;
        this.correoAutorPub = correoAutorPub;
        this.correoMiembro = correoMiembro;
    }

    // Getters and Setters
    public String getIdComentario() { return idComentario; }
    public void setIdComentario(String idComentario) { this.idComentario = idComentario; }

    public String getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(String idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getCorreoAutorPub() { return correoAutorPub; }
    public void setCorreoAutorPub(String correoAutorPub) { this.correoAutorPub = correoAutorPub; }

    public String getCorreoMiembro() { return correoMiembro; }
    public void setCorreoMiembro(String correoMiembro) { this.correoMiembro = correoMiembro; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentId commentId = (CommentId) o;
        return Objects.equals(idComentario, commentId.idComentario) &&
                Objects.equals(idPublicacion, commentId.idPublicacion) &&
                Objects.equals(correoAutorPub, commentId.correoAutorPub) &&
                Objects.equals(correoMiembro, commentId.correoMiembro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idComentario, idPublicacion, correoAutorPub, correoMiembro);
    }
}
