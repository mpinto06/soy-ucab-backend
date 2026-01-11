package com.ucab.soy_ucab_backend.model;

import java.io.Serializable;
import java.util.Objects;

public class PublicacionId implements Serializable {
    private String idPublicacion;
    private String correoAutor;

    public PublicacionId() {}

    public PublicacionId(String idPublicacion, String correoAutor) {
        this.idPublicacion = idPublicacion;
        this.correoAutor = correoAutor;
    }

    public String getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(String idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getCorreoAutor() { return correoAutor; }
    public void setCorreoAutor(String correoAutor) { this.correoAutor = correoAutor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicacionId that = (PublicacionId) o;
        return Objects.equals(idPublicacion, that.idPublicacion) &&
               Objects.equals(correoAutor, that.correoAutor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPublicacion, correoAutor);
    }
}
