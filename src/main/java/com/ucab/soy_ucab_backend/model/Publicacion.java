package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "Publicacion")
@IdClass(PublicacionId.class)
public class Publicacion {
    @Id
    @Column(name = "id_publicacion", length = 50)
    private String idPublicacion;

    @Id
    @Column(name = "correo_autor", length = 255)
    private String correoAutor;

    @Column(name = "id_grupo", length = 255)
    private String idGrupo;

    @Column(name = "texto_pub", length = 280)
    private String textoPub;

    @Column(name = "fecha_hora")
    private OffsetDateTime fechaHora;

    @Column(name = "total_likes")
    private Integer totalLikes;

    @Column(name = "total_comen")
    private Integer totalComen;

    // Getters and Setters
    public String getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(String idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getCorreoAutor() { return correoAutor; }
    public void setCorreoAutor(String correoAutor) { this.correoAutor = correoAutor; }

    public String getIdGrupo() { return idGrupo; }
    public void setIdGrupo(String idGrupo) { this.idGrupo = idGrupo; }

    public String getTextoPub() { return textoPub; }
    public void setTextoPub(String textoPub) { this.textoPub = textoPub; }

    public OffsetDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(OffsetDateTime fechaHora) { this.fechaHora = fechaHora; }

    public Integer getTotalLikes() { return totalLikes; }
    public void setTotalLikes(Integer totalLikes) { this.totalLikes = totalLikes; }

    public Integer getTotalComen() { return totalComen; }
    public void setTotalComen(Integer totalComen) { this.totalComen = totalComen; }
}
