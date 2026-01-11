package com.ucab.soy_ucab_backend.model;

import java.io.Serializable;
import java.util.Objects;

public class TrataSobreId implements Serializable {
    private String idPublicacion;
    private String correoAutor;
    private String nombreInteres;

    public TrataSobreId() {}

    public TrataSobreId(String idPublicacion, String correoAutor, String nombreInteres) {
        this.idPublicacion = idPublicacion;
        this.correoAutor = correoAutor;
        this.nombreInteres = nombreInteres;
    }

    // Getters and Setters
    public String getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(String idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getCorreoAutor() { return correoAutor; }
    public void setCorreoAutor(String correoAutor) { this.correoAutor = correoAutor; }

    public String getNombreInteres() { return nombreInteres; }
    public void setNombreInteres(String nombreInteres) { this.nombreInteres = nombreInteres; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrataSobreId that = (TrataSobreId) o;
        return Objects.equals(idPublicacion, that.idPublicacion) &&
               Objects.equals(correoAutor, that.correoAutor) &&
               Objects.equals(nombreInteres, that.nombreInteres);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPublicacion, correoAutor, nombreInteres);
    }
}
