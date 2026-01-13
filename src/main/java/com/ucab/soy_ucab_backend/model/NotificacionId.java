package com.ucab.soy_ucab_backend.model;

import java.io.Serializable;
import java.util.Objects;

public class NotificacionId implements Serializable {
    private String idNotificacion;
    private String correoDestinatario;

    public NotificacionId() {
    }

    public NotificacionId(String idNotificacion, String correoDestinatario) {
        this.idNotificacion = idNotificacion;
        this.correoDestinatario = correoDestinatario;
    }

    // Getters and Setters
    public String getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(String idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getCorreoDestinatario() {
        return correoDestinatario;
    }

    public void setCorreoDestinatario(String correoDestinatario) {
        this.correoDestinatario = correoDestinatario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NotificacionId that = (NotificacionId) o;
        return Objects.equals(idNotificacion, that.idNotificacion) &&
                Objects.equals(correoDestinatario, that.correoDestinatario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNotificacion, correoDestinatario);
    }
}
