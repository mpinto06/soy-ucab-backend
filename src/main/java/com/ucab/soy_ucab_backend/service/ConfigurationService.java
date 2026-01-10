package com.ucab.soy_ucab_backend.service;

import com.ucab.soy_ucab_backend.dto.ConfigurationDto;
import com.ucab.soy_ucab_backend.model.Miembro;
import com.ucab.soy_ucab_backend.repository.MiembroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ConfigurationService {

    @Autowired
    private MiembroRepository miembroRepository;

    public ConfigurationDto getConfiguration(String email) {
        Miembro miembro = miembroRepository.findById(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        String privacy = mapToDtoPrivacy(miembro.getPrivacidadMensajes());

        return new ConfigurationDto(
                privacy,
                Boolean.TRUE.equals(miembro.getNotifPublicaciones()),
                Boolean.TRUE.equals(miembro.getNotifInteracciones()),
                Boolean.TRUE.equals(miembro.getNotifOfertas()),
                Boolean.TRUE.equals(miembro.getNotifEventos()),
                Boolean.TRUE.equals(miembro.getNotifSeguidores()),
                Boolean.TRUE.equals(miembro.getNotifAmigos())
        );
    }

    public ConfigurationDto updateConfiguration(String email, ConfigurationDto dto) {
        Miembro miembro = miembroRepository.findById(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        miembro.setPrivacidadMensajes(mapToEntityPrivacy(dto.messagePrivacy()));
        miembro.setNotifPublicaciones(dto.posts());
        miembro.setNotifInteracciones(dto.interactions());
        miembro.setNotifOfertas(dto.jobs());
        miembro.setNotifEventos(dto.events());
        miembro.setNotifSeguidores(dto.newFollowers());
        miembro.setNotifAmigos(dto.newFriends());

        miembroRepository.save(miembro);

        return dto;
    }

    private String mapToDtoPrivacy(String dbValue) {
        if (dbValue == null) return "anyone";
        return switch (dbValue) {
            case "cualquiera" -> "anyone";
            case "solo_amigos" -> "friends";
            case "nadie" -> "none";
            default -> "anyone";
        };
    }

    private String mapToEntityPrivacy(String dtoValue) {
        if (dtoValue == null) return "cualquiera";
        return switch (dtoValue) {
            case "anyone" -> "cualquiera";
            case "friends" -> "solo_amigos";
            case "none" -> "nadie";
            default -> "cualquiera";
        };
    }
}
