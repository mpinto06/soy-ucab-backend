package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Miembro")
@Inheritance(strategy = InheritanceType.JOINED)
public class Miembro {
    @Id
    @Column(name = "correo_electronico")
    private String email;

    @Column(name = "hash_contrasena")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_acceso", columnDefinition = "rol_sistema")
    private RoleEnum role = RoleEnum.usuario_estandar; // Default

    // Other fields can be ignored for auth basics
}
