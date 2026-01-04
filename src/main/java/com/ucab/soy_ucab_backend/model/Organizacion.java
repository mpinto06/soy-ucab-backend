package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "Organizacion")
@PrimaryKeyJoinColumn(name = "correo_electronico")
@Data
@EqualsAndHashCode(callSuper = true)
public class Organizacion extends Miembro {
    @Column(name = "nombre_organizacion")
    private String name;

    @Column(name = "descripcion_org")
    private String description;
}
