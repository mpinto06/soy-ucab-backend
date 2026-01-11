package com.ucab.soy_ucab_backend.model;

import java.io.Serializable;
import java.util.Objects;

public class PerteneceId implements Serializable {
    private String nombreGrupo;
    private String correoMiembro;

    public PerteneceId() {}

    public PerteneceId(String nombreGrupo, String correoMiembro) {
        this.nombreGrupo = nombreGrupo;
        this.correoMiembro = correoMiembro;
    }

    // Getters, Setters, Equals, HashCode
    public String getNombreGrupo() { return nombreGrupo; }
    public void setNombreGrupo(String nombreGrupo) { this.nombreGrupo = nombreGrupo; }

    public String getCorreoMiembro() { return correoMiembro; }
    public void setCorreoMiembro(String correoMiembro) { this.correoMiembro = correoMiembro; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerteneceId that = (PerteneceId) o;
        return Objects.equals(nombreGrupo, that.nombreGrupo) &&
               Objects.equals(correoMiembro, that.correoMiembro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreGrupo, correoMiembro);
    }
}
