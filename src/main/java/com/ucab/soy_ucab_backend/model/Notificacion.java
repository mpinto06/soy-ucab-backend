package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "Notificacion")
@IdClass(NotificacionId.class)
public class Notificacion {
    @Id
    @Column(name = "id_notificacion", length = 50)
    private String idNotificacion;

    @Id
    @Column(name = "correo_destinatario", length = 255)
    private String correoDestinatario;

    @Column(name = "texto_notificacion", length = 255)
    private String textoNotificacion;

    @Column(name = "fecha_hora")
    private OffsetDateTime fechaHora;

    @Column(name = "leida")
    private Boolean leida;

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

    public String getTextoNotificacion() {
        return textoNotificacion;
    }

    public void setTextoNotificacion(String textoNotificacion) {
        this.textoNotificacion = textoNotificacion;
    }

    public OffsetDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(OffsetDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }
}
