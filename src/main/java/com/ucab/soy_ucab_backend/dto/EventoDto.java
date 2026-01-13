package com.ucab.soy_ucab_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ucab.soy_ucab_backend.model.EstadoEvento;
import com.ucab.soy_ucab_backend.model.ModalidadEvento;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class EventoDto {
    @JsonProperty("nombre_evento")
    private String nombreEvento;

    @JsonProperty("correo_organizador")
    private String correoOrganizador;

    private String descripcion;
    private ModalidadEvento modalidad;

    @JsonProperty("fecha_inicio")
    private OffsetDateTime fechaInicio;

    @JsonProperty("fecha_fin")
    private OffsetDateTime fechaFin;

    private String ubicacion;

    @JsonProperty("estado_evento")
    private EstadoEvento estadoEvento;

    @JsonProperty("url_conferencia")
    private String urlConferencia;
}
