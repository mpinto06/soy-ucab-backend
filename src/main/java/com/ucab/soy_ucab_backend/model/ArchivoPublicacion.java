package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Archivo_Publicacion")
@IdClass(ArchivoPublicacionId.class)
public class ArchivoPublicacion {
    @Id
    @Column(name = "id_publicacion", length = 50)
    private String idPublicacion;

    @Id
    @Column(name = "correo_autor", length = 255)
    private String correoAutor;

    @Id
    @Column(name = "nombre_archivo", length = 50)
    private String nombreArchivo;

    @Id
    @Column(name = "formato_archivo") // Enum type handled as string usually or with converter
    private String formatoArchivo; // Should match DB Enum 'extension_publicacion' if simplified to String for JPA

    @Column(name = "archivo")
    private byte[] archivo;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "id_publicacion", referencedColumnName = "id_publicacion", insertable = false, updatable = false),
        @JoinColumn(name = "correo_autor", referencedColumnName = "correo_autor", insertable = false, updatable = false)
    })
    private Publicacion publicacion;

    public void setId(ArchivoPublicacionId id) {
        this.idPublicacion = id.getIdPublicacion();
        this.correoAutor = id.getCorreoAutor();
        this.nombreArchivo = id.getNombreArchivo();
        this.formatoArchivo = id.getFormatoArchivo();
    }

    // Getters and Setters
    public String getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(String idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getCorreoAutor() { return correoAutor; }
    public void setCorreoAutor(String correoAutor) { this.correoAutor = correoAutor; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getFormatoArchivo() { return formatoArchivo; }
    public void setFormatoArchivo(String formatoArchivo) { this.formatoArchivo = formatoArchivo; }

    public byte[] getArchivo() { return archivo; }
    public void setArchivo(byte[] archivo) { this.archivo = archivo; }

    public Publicacion getPublicacion() { return publicacion; }
    public void setPublicacion(Publicacion publicacion) { this.publicacion = publicacion; }
}
