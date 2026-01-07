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

    @Column(name = "archivo_foto")
    private byte[] archivoFoto;

    @Column(name = "formato_foto")
    private String formatoFoto; // We can treat enum as String or map it properly, String is easier for read-only scenario here

    @Column(name = "encabezado_perfil")
    private String encabezadoPerfil;

    @ElementCollection
    @CollectionTable(
        name = "Expresa",
        joinColumns = @JoinColumn(name = "correo_miembro", referencedColumnName = "correo_electronico")
    )
    @Column(name = "nombre_interes")
    private java.util.List<String> interests;

    // Other fields can be ignored for auth basics
}
