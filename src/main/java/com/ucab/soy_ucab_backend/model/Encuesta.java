package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "Encuesta")
@IdClass(EncuestaId.class)
public class Encuesta {
    @Id
    @Column(name = "id_publicacion", length = 50)
    private String idPublicacion;

    @Id
    @Column(name = "correo_autor", length = 255)
    private String correoAutor;

    @Column(name = "fecha_hora_fin")
    private OffsetDateTime fechaHoraFin;

    @OneToOne
    @JoinColumns({
        @JoinColumn(name = "id_publicacion", referencedColumnName = "id_publicacion", insertable = false, updatable = false),
        @JoinColumn(name = "correo_autor", referencedColumnName = "correo_autor", insertable = false, updatable = false)
    })
    private Publicacion publicacion;

    public void setId(EncuestaId id) {
        this.idPublicacion = id.getIdPublicacion();
        this.correoAutor = id.getCorreoAutor();
    }

    // Getters and Setters
    public String getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(String idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getCorreoAutor() { return correoAutor; }
    public void setCorreoAutor(String correoAutor) { this.correoAutor = correoAutor; }

    public OffsetDateTime getFechaHoraFin() { return fechaHoraFin; }
    public void setFechaHoraFin(OffsetDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }

    public Publicacion getPublicacion() { return publicacion; }
    public void setPublicacion(Publicacion publicacion) { this.publicacion = publicacion; }
}
