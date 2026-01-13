package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Grupo")
public class Grupo {

    @Id
    @Column(name = "nombre_grupo")
    private String nombreGrupo;

    @Column(name = "descripcion")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_grupo")
    private TipoGrupo tipoGrupo;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "correo_creador", nullable = false)
    private Miembro creador;

    public Grupo() {
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Miembro getCreador() {
        return creador;
    }

    public void setCreador(Miembro creador) {
        this.creador = creador;
    }
}
