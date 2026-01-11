package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "Comenta")
@IdClass(CommentId.class)
public class Comment {

    @Id
    @Column(name = "id_comentario")
    private String idComentario;

    @Id
    @Column(name = "id_publicacion")
    private String idPublicacion;

    @Id
    @Column(name = "correo_autor_pub")
    private String correoAutorPub;

    @Id
    @Column(name = "correo_miembro")
    private String correoMiembro;

    @Column(name = "texto_comentario")
    private String textoComentario;

    @Column(name = "fecha_hora")
    private OffsetDateTime fechaHora;

    public Comment() {}

    public Comment(String idComentario, String idPublicacion, String correoAutorPub, String correoMiembro, String textoComentario, OffsetDateTime fechaHora) {
        this.idComentario = idComentario;
        this.idPublicacion = idPublicacion;
        this.correoAutorPub = correoAutorPub;
        this.correoMiembro = correoMiembro;
        this.textoComentario = textoComentario;
        this.fechaHora = fechaHora;
    }

    public String getIdComentario() { return idComentario; }
    public void setIdComentario(String idComentario) { this.idComentario = idComentario; }

    public String getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(String idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getCorreoAutorPub() { return correoAutorPub; }
    public void setCorreoAutorPub(String correoAutorPub) { this.correoAutorPub = correoAutorPub; }

    public String getCorreoMiembro() { return correoMiembro; }
    public void setCorreoMiembro(String correoMiembro) { this.correoMiembro = correoMiembro; }

    public String getTextoComentario() { return textoComentario; }
    public void setTextoComentario(String textoComentario) { this.textoComentario = textoComentario; }

    public OffsetDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(OffsetDateTime fechaHora) { this.fechaHora = fechaHora; }
}
