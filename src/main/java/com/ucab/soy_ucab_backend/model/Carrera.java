package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Carrera")
public class Carrera {
    @Id
    @Column(name = "nombre_carrera")
    private String nombreCarrera;

    @Column(name = "nivel_carrera")
    @Enumerated(jakarta.persistence.EnumType.STRING)
    private NivelCarrera nivelCarrera;

    @Column(name = "correo_electronico") // This references Escuela
    private String correoEscuela;
    
    public enum NivelCarrera {
        pregrado, posgrado
    }
}
