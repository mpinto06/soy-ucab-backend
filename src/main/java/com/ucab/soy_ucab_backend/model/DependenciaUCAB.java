package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "Dependencia_UCAB")
@PrimaryKeyJoinColumn(name = "correo_electronico")
@Data
@EqualsAndHashCode(callSuper = true)
public class DependenciaUCAB extends Organizacion {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entidad_institucional", columnDefinition = "tipo_entidad")
    private TipoEntidad tipoEntidad;
}
