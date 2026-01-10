package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "Periodo_Educativo")
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumns({
    @PrimaryKeyJoinColumn(name = "id_periodo", referencedColumnName = "id_periodo"),
    @PrimaryKeyJoinColumn(name = "correo_persona", referencedColumnName = "correo_persona")
})
public class PeriodoEducativo extends Periodo {
    @Column(name = "id_carrera")
    private String idCarrera; // Could be mapped to Carrera entity, but String is fine for read

    @Column(name = "archivo_certificado")
    private byte[] archivoCertificado;

    @Column(name = "formato_archivo")
    private String formatoArchivo;

    @Column(name = "nombre_estudio")
    private String nombreEstudio;

    @Transient
    private String careerLevel;

    @Transient
    private String schoolName;
}
