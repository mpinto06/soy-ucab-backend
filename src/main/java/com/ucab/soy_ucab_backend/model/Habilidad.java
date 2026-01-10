package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Habilidad")
public class Habilidad {
    @Id
    @Column(name = "nombre_habilidad")
    private String nombreHabilidad;
}
