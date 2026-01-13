package com.ucab.soy_ucab_backend.model;

public enum EstadoEvento {
    borrador("borrador"),
    publicado("publicado"),
    en_curso("en curso"),
    finalizado("finalizado"),
    archivado("archivado");

    private final String dbValue;

    EstadoEvento(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static EstadoEvento fromDbValue(String dbValue) {
        for (EstadoEvento estado : EstadoEvento.values()) {
            if (estado.dbValue.equals(dbValue)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Unknown database value: " + dbValue);
    }
}
