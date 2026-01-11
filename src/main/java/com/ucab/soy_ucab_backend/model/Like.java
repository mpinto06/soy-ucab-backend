package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "Me_Gusta")
@IdClass(LikeId.class)
public class Like {

    @Id
    @Column(name = "correo_miembro")
    private String correoMiembro;

    @Id
    @Column(name = "id_publicacion")
    private String idPublicacion;

    @Id
    @Column(name = "correo_autor_pub")
    private String correoAutorPub;

    @Column(name = "fecha_hora")
    private Instant fechaMeGusta;

    public Like() {}

    public Like(String correoMiembro, String idPublicacion, String correoAutorPub) {
        this.correoMiembro = correoMiembro;
        this.idPublicacion = idPublicacion;
        this.correoAutorPub = correoAutorPub;
        this.fechaMeGusta = Instant.now();
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

    public Instant getFechaMeGusta() {
        return fechaMeGusta;
    }

    public void setFechaMeGusta(Instant fechaMeGusta) {
        this.fechaMeGusta = fechaMeGusta;
    }
}
