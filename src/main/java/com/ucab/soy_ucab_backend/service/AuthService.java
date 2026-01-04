package com.ucab.soy_ucab_backend.service;

import com.ucab.soy_ucab_backend.dto.auth.AuthResponseDto;
import com.ucab.soy_ucab_backend.dto.auth.LoginRequestDto;
import com.ucab.soy_ucab_backend.dto.auth.RegisterOrgRequestDto;
import com.ucab.soy_ucab_backend.dto.auth.RegisterUserRequestDto;
import com.ucab.soy_ucab_backend.model.Miembro;
import com.ucab.soy_ucab_backend.model.Organizacion;
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

        // Return simple token (userId/email) as per simplified requirement
        return new AuthResponseDto(miembro.getEmail(), miembro.getRole().name(), miembro.getEmail());
    }

    public AuthResponseDto registerUser(RegisterUserRequestDto request) {
        if (miembroRepository.existsById(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo electrónico ya está registrado");
        }

        Persona persona = new Persona();
        persona.setEmail(request.getEmail());
        persona.setPassword(passwordEncoder.encode(request.getPassword()));
        persona.setFirstName(request.getFirstName());
        persona.setLastName(request.getLastName()); // Default for now if not in DTO
        // Optional fields
        if(request.getBirthDate() != null) persona.setBirthDate(request.getBirthDate());
        if(request.getGender() != null) persona.setGender(request.getGender());
        
        personaRepository.save(persona);

        return new AuthResponseDto(persona.getEmail(), persona.getRole().name(), persona.getEmail());
    }

    public AuthResponseDto registerOrg(RegisterOrgRequestDto request) {
         if (miembroRepository.existsById(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo electrónico ya está registrado");
        }

        Organizacion org = new Organizacion();
        org.setEmail(request.getEmail());
        org.setPassword(passwordEncoder.encode(request.getPassword()));
        org.setName(request.getOrgName() != null ? request.getOrgName() : request.getEmail()); // Fallback
        org.setDescription(request.getDescription());
        
        // Note: Logic for 'Dependencia_UCAB' vs 'Organizacion_Asociada' tables (subclasses) 
        // would go here if we were implementing the full inheritance mapping. 
        // For now, saving as base Organizacion.
        
        organizacionRepository.save(org);

        return new AuthResponseDto(org.getEmail(), org.getRole().name(), org.getEmail());
    }
}
