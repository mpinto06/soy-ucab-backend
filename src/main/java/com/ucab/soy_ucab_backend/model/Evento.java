package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "Evento")
@Getter
@Setter
public class Evento {

    @Id
    @Column(name = "nombre_evento", length = 255)
    private String nombreEvento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "correo_organizador", nullable = false)
    private Miembro organizador;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidad")
    private ModalidadEvento modalidad;

    @Column(name = "fecha_inicio", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime fechaInicio;

    @Column(name = "fecha_fin", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime fechaFin;

    @Column(name = "ubicacion", length = 255)
    private String ubicacion;

    @Column(name = "estado_evento")
    private EstadoEvento estadoEvento;

    @Column(name = "url_conferencia", length = 255)
    private String urlConferencia;

}
