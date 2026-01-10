package com.ucab.soy_ucab_backend.service;

import com.ucab.soy_ucab_backend.dto.auth.AuthResponseDto;
import com.ucab.soy_ucab_backend.dto.auth.LoginRequestDto;
import com.ucab.soy_ucab_backend.dto.auth.RegisterOrgRequestDto;
import com.ucab.soy_ucab_backend.dto.auth.RegisterUserRequestDto;
import com.ucab.soy_ucab_backend.model.DependenciaUCAB;
import com.ucab.soy_ucab_backend.model.Miembro;
import com.ucab.soy_ucab_backend.model.Organizacion;
import com.ucab.soy_ucab_backend.model.OrganizacionAsociada;
import com.ucab.soy_ucab_backend.model.Persona;
import com.ucab.soy_ucab_backend.repository.MiembroRepository;
import com.ucab.soy_ucab_backend.repository.OrganizacionRepository;
import com.ucab.soy_ucab_backend.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private MiembroRepository miembroRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private OrganizacionRepository organizacionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponseDto login(LoginRequestDto request) {
        Optional<Miembro> miembroOpt = miembroRepository.findByEmail(request.getEmail());

        if (miembroOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        Miembro miembro = miembroOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), miembro.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String memberType = "Miembro";
        if (miembro instanceof Persona) {
            memberType = "Persona";
        } else if (miembro instanceof DependenciaUCAB) {
            memberType = "DependenciaUCAB";
        } else if (miembro instanceof OrganizacionAsociada) {
            memberType = "OrganizacionAsociada";
        } else if (miembro instanceof Organizacion) {
            memberType = "Organizacion";
        }

        return new AuthResponseDto(miembro.getEmail(), miembro.getRole().name(), memberType, miembro);
    }

    public AuthResponseDto registerUser(RegisterUserRequestDto request) {
        if (miembroRepository.existsById(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo electrónico ya está registrado");
        }

        Persona persona = new Persona();
        persona.setEmail(request.getEmail());
        persona.setPassword(passwordEncoder.encode(request.getPassword()));
        persona.setFirstName(request.getFirstName());
        persona.setLastName(request.getLastName());
        if(request.getBirthDate() != null) persona.setBirthDate(request.getBirthDate());
        if(request.getGender() != null) persona.setGender(request.getGender());
        
        personaRepository.save(persona);

        return new AuthResponseDto(persona.getEmail(), persona.getRole().name(), "Persona", persona);
    }

    public AuthResponseDto registerOrg(RegisterOrgRequestDto request) {
         if (miembroRepository.existsById(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo electrónico ya está registrado");
        }

        Organizacion org = new Organizacion();
        org.setEmail(request.getEmail());
        org.setPassword(passwordEncoder.encode(request.getPassword()));
        org.setName(request.getOrgName() != null ? request.getOrgName() : request.getEmail());
        org.setDescription(request.getDescription());
        
        organizacionRepository.save(org);

        return new AuthResponseDto(org.getEmail(), org.getRole().name(), "Organizacion", org);
    }
}
