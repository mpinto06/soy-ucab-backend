package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "Escuela")
@PrimaryKeyJoinColumn(name = "correo_electronico")
@Data
@EqualsAndHashCode(callSuper = true)
public class Escuela extends DependenciaUCAB {
    
    @Column(name = "correo_facultad")
    private String correoFacultad;
}
