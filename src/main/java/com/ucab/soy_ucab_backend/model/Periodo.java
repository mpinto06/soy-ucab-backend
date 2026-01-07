package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "Periodo")
@IdClass(PeriodoId.class)
@Inheritance(strategy = InheritanceType.JOINED)
public class Periodo {
    @Id
    @Column(name = "id_periodo")
    private String idPeriodo;

    @Id
    @Column(name = "correo_persona")
    private String correoPersona;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "descripcion_periodo")
    private String descripcionPeriodo;

    @ElementCollection
    @CollectionTable(
        name = "Demuestra",
        joinColumns = {
            @JoinColumn(name = "id_periodo", referencedColumnName = "id_periodo"),
            @JoinColumn(name = "correo_persona", referencedColumnName = "correo_persona")
        }
    )
    @Column(name = "nombre_habilidad")
    private java.util.List<String> skills;
}
