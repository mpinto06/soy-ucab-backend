package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "Es_Amigo")
@IdClass(EsAmigoId.class)
@Data
public class EsAmigo {

    @Id
    @Column(name = "correo_persona1")
    private String person1Email;

    @Id
    @Column(name = "correo_persona2")
    private String person2Email;

    @Column(name = "estado")
    private String status;

    @Column(name = "fecha_solicitud", insertable = false, updatable = false)
    private LocalDate requestDate;
}
