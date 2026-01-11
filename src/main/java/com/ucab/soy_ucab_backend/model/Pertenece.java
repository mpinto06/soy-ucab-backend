package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Pertenece")
@IdClass(PerteneceId.class)
public class Pertenece {
    @Id
    @Column(name = "nombre_grupo", length = 255)
    private String nombreGrupo;

    @Id
    @Column(name = "correo_miembro", length = 255)
    private String correoMiembro;

    @Column(name = "rol_en_grupo")
    private String rol;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    // Getters and Setters
    public String getNombreGrupo() { return nombreGrupo; }
    public void setNombreGrupo(String nombreGrupo) { this.nombreGrupo = nombreGrupo; }

    public String getCorreoMiembro() { return correoMiembro; }
    public void setCorreoMiembro(String correoMiembro) { this.correoMiembro = correoMiembro; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
}
