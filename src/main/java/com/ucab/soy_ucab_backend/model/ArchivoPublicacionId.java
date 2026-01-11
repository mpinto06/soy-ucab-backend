package com.ucab.soy_ucab_backend.model;

import java.io.Serializable;
import java.util.Objects;

public class ArchivoPublicacionId implements Serializable {
    private String idPublicacion;
    private String correoAutor;
    private String nombreArchivo;
    private String formatoArchivo;

    public ArchivoPublicacionId() {}

    public ArchivoPublicacionId(String idPublicacion, String correoAutor, String nombreArchivo, String formatoArchivo) {
        this.idPublicacion = idPublicacion;
        this.correoAutor = correoAutor;
        this.nombreArchivo = nombreArchivo;
        this.formatoArchivo = formatoArchivo;
    }

    // Getters, Setters, Equals, HashCode
    public String getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(String idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getCorreoAutor() { return correoAutor; }
    public void setCorreoAutor(String correoAutor) { this.correoAutor = correoAutor; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getFormatoArchivo() { return formatoArchivo; }
    public void setFormatoArchivo(String formatoArchivo) { this.formatoArchivo = formatoArchivo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArchivoPublicacionId that = (ArchivoPublicacionId) o;
        return Objects.equals(idPublicacion, that.idPublicacion) &&
               Objects.equals(correoAutor, that.correoAutor) &&
               Objects.equals(nombreArchivo, that.nombreArchivo) &&
               Objects.equals(formatoArchivo, that.formatoArchivo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPublicacion, correoAutor, nombreArchivo, formatoArchivo);
    }
}
