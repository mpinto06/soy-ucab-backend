package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "Organizacion_Asociada")
@PrimaryKeyJoinColumn(name = "correo_electronico")
@Data
@EqualsAndHashCode(callSuper = true)
public class OrganizacionAsociada extends Organizacion {

    @Column(name = "RIF")
    private String rif;
}
