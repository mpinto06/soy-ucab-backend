package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "Sigue")
@IdClass(SigueId.class)
@Data
public class Sigue {

    @Id
    @Column(name = "correo_seguidor")
    private String followerEmail;

    @Id
    @Column(name = "correo_seguido")
    private String followedEmail;

    @Column(name = "fecha_hora", insertable = false, updatable = false)
    private LocalDateTime timestamp;
}
