package com.ucab.soy_ucab_backend.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoEventoConverter implements AttributeConverter<EstadoEvento, String> {

    @Override
    public String convertToDatabaseColumn(EstadoEvento attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDbValue();
    }

    @Override
    public EstadoEvento convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return EstadoEvento.fromDbValue(dbData);
    }
}
