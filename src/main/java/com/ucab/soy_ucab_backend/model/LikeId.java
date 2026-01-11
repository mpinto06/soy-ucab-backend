package com.ucab.soy_ucab_backend.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;

public class LikeId implements Serializable {
    private String correoMiembro;
    private String idPublicacion;
    private String correoAutorPub;

    public LikeId() {}

    public LikeId(String correoMiembro, String idPublicacion, String correoAutorPub) {
        this.correoMiembro = correoMiembro;
        this.idPublicacion = idPublicacion;
        this.correoAutorPub = correoAutorPub;
    }

    public String getCorreoMiembro() {
        return correoMiembro;
    }

    public void setCorreoMiembro(String correoMiembro) {
        this.correoMiembro = correoMiembro;
    }

    public String getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(String idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public String getCorreoAutorPub() {
        return correoAutorPub;
    }

    public void setCorreoAutorPub(String correoAutorPub) {
        this.correoAutorPub = correoAutorPub;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeId likeId = (LikeId) o;
        return Objects.equals(correoMiembro, likeId.correoMiembro) &&
                Objects.equals(idPublicacion, likeId.idPublicacion) &&
                Objects.equals(correoAutorPub, likeId.correoAutorPub);
    }

    @Override
    public int hashCode() {
        return Objects.hash(correoMiembro, idPublicacion, correoAutorPub);
    }
}
