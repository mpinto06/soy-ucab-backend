package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Grupo")
public class Grupo {
    @Id
    @Column(name = "nombre_grupo", length = 255)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "tipo_grupo")
    private String tipo; // Enum type as String

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "correo_creador", length = 255)
    private String correoCreador;

    // Getters and Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getCorreoCreador() { return correoCreador; }
    public void setCorreoCreador(String correoCreador) { this.correoCreador = correoCreador; }
}
