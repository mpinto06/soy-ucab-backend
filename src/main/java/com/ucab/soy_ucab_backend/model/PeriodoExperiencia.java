package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "Periodo_Experiencia")
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumns({
    @PrimaryKeyJoinColumn(name = "id_periodo", referencedColumnName = "id_periodo"),
    @PrimaryKeyJoinColumn(name = "correo_persona", referencedColumnName = "correo_persona")
})
public class PeriodoExperiencia extends Periodo {
    @Column(name = "correo_organizacion")
    private String correoOrganizacion; // Could map to Organizacion, simplified for now

    @Column(name = "tipo_cargo")
    private String tipoCargo; // Enum in DB

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "archivo_carta")
    private byte[] archivoCarta;

    @Column(name = "formato_archivo")
    private String formatoArchivo;
}
