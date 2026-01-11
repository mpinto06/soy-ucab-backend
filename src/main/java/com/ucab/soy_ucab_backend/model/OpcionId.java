package com.ucab.soy_ucab_backend.model;

import java.io.Serializable;
import java.util.Objects;

public class OpcionId implements Serializable {
    private String idPublicacion;
    private String correoAutor;
    private String textoOpcion;

    public OpcionId() {}

    public OpcionId(String idPublicacion, String correoAutor, String textoOpcion) {
        this.idPublicacion = idPublicacion;
        this.correoAutor = correoAutor;
        this.textoOpcion = textoOpcion;
    }

    // Getters, Setters, Equals, HashCode
    public String getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(String idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getCorreoAutor() { return correoAutor; }
    public void setCorreoAutor(String correoAutor) { this.correoAutor = correoAutor; }

    public String getTextoOpcion() { return textoOpcion; }
    public void setTextoOpcion(String textoOpcion) { this.textoOpcion = textoOpcion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpcionId opcionId = (OpcionId) o;
        return Objects.equals(idPublicacion, opcionId.idPublicacion) &&
               Objects.equals(correoAutor, opcionId.correoAutor) &&
               Objects.equals(textoOpcion, opcionId.textoOpcion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPublicacion, correoAutor, textoOpcion);
    }
}
