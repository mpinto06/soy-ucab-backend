package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(name = "Persona")
@PrimaryKeyJoinColumn(name = "correo_electronico")
@Data
@EqualsAndHashCode(callSuper = true)
public class Persona extends Miembro {
    @Column(name = "primer_nombre")
    private String firstName;

    @Column(name = "primer_apellido")
    private String lastName;
    
    @Column(name = "fecha_nacimiento")
    private LocalDate birthDate;
    
    @Column(name = "sexo")
    private String gender; // 'M' or 'F' (using String for simplicity mapping PG Enum, could be Enum)
    
    @Column(name = "ubicacion_geografica")
    private String location = "Unknown"; // Default
}
