package com.ucab.soy_ucab_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoId implements Serializable {
    private String idPeriodo;
    private String correoPersona;
}
