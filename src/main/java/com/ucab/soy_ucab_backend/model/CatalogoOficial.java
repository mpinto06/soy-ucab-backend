package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Catalogo_Oficial_UCAB")
public class CatalogoOficial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_catalogo")
    private Long id;

    @Column(name = "nombre_escuela", unique = true, nullable = false)
    private String nombreEscuela;

    @Column(name = "nombre_facultad", nullable = false)
    private String nombreFacultad;
}
