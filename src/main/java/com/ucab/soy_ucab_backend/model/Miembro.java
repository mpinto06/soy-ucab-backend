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

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(name = "hash_contrasena")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_acceso", columnDefinition = "rol_sistema")
    private RoleEnum role = RoleEnum.usuario_estandar; // Default

    @Column(name = "archivo_foto")
    private byte[] archivoFoto;

    @Column(name = "formato_foto")
    private String formatoFoto; // We can treat enum as String or map it properly, String is easier for read-only scenario here

    @Column(name = "nombre_archivo_foto")
    private String nombreArchivoFoto;

    @Column(name = "encabezado_perfil")
    private String encabezadoPerfil;

    @ElementCollection
    @CollectionTable(
        name = "Expresa",
        joinColumns = @JoinColumn(name = "correo_miembro", referencedColumnName = "correo_electronico")
    )
    @Column(name = "nombre_interes")
    private java.util.List<String> interests;

    @Column(name = "privacidad_mensajes")
    private String privacidadMensajes; // "cualquiera", "solo_amigos", "nadie" - handling as String for simplicity or create Enum

    @Column(name = "notif_publicaciones")
    private Boolean notifPublicaciones = true;

    @Column(name = "notif_eventos")
    private Boolean notifEventos = true;

    @Column(name = "notif_seguidores")
    private Boolean notifSeguidores = true;

    @Column(name = "notif_amigos")
    private Boolean notifAmigos = true;

    @Column(name = "notif_interacciones")
    private Boolean notifInteracciones = true;

    @Column(name = "notif_ofertas")
    private Boolean notifOfertas = true;

    // Other fields can be ignored for auth basics
}
