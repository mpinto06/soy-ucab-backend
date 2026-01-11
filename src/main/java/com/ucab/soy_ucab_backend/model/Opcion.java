package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Opcion")
@IdClass(OpcionId.class)
public class Opcion {
    @Id
    @Column(name = "id_publicacion", length = 50)
    private String idPublicacion;

    @Id
    @Column(name = "correo_autor", length = 255)
    private String correoAutor;

    @Id
    @Column(name = "texto_opcion", length = 100)
    private String textoOpcion;

    @Column(name = "total_votos")
    private Integer totalVotos;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "id_publicacion", referencedColumnName = "id_publicacion", insertable = false, updatable = false),
        @JoinColumn(name = "correo_autor", referencedColumnName = "correo_autor", insertable = false, updatable = false)
    })
    private Encuesta encuesta;

    public void setId(OpcionId id) {
        this.idPublicacion = id.getIdPublicacion();
        this.correoAutor = id.getCorreoAutor();
        this.textoOpcion = id.getTextoOpcion();
    }

    // Getters and Setters
    public String getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(String idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getCorreoAutor() { return correoAutor; }
    public void setCorreoAutor(String correoAutor) { this.correoAutor = correoAutor; }

    public String getTextoOpcion() { return textoOpcion; }
    public void setTextoOpcion(String textoOpcion) { this.textoOpcion = textoOpcion; }

    public Integer getTotalVotos() { return totalVotos; }
    public void setTotalVotos(Integer totalVotos) { this.totalVotos = totalVotos; }

    public Encuesta getEncuesta() { return encuesta; }
    public void setEncuesta(Encuesta encuesta) { this.encuesta = encuesta; }
}
