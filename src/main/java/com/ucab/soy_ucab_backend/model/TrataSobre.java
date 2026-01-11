package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Trata_Sobre")
@IdClass(TrataSobreId.class)
public class TrataSobre {
    @Id
    @Column(name = "id_publicacion", length = 50)
    private String idPublicacion;

    @Id
    @Column(name = "correo_autor", length = 255)
    private String correoAutor;

    @Id
    @Column(name = "nombre_interes", length = 50)
    private String nombreInteres;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "id_publicacion", referencedColumnName = "id_publicacion", insertable = false, updatable = false),
        @JoinColumn(name = "correo_autor", referencedColumnName = "correo_autor", insertable = false, updatable = false)
    })
    private Publicacion publicacion;

    @ManyToOne
    @JoinColumn(name = "nombre_interes", referencedColumnName = "nombre_interes", insertable = false, updatable = false)
    private Interes interes;

    // Getters and Setters
    public String getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(String idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getCorreoAutor() { return correoAutor; }
    public void setCorreoAutor(String correoAutor) { this.correoAutor = correoAutor; }

    public String getNombreInteres() { return nombreInteres; }
    public void setNombreInteres(String nombreInteres) { this.nombreInteres = nombreInteres; }

    public Publicacion getPublicacion() { return publicacion; }
    public void setPublicacion(Publicacion publicacion) { this.publicacion = publicacion; }

    public Interes getInteres() { return interes; }
    public void setInteres(Interes interes) { this.interes = interes; }
    
    // Helper to set ID from composite key logic if needed, or set individual fields
    public void setId(TrataSobreId id) {
        this.idPublicacion = id.getIdPublicacion();
        this.correoAutor = id.getCorreoAutor();
        this.nombreInteres = id.getNombreInteres();
    }
}
