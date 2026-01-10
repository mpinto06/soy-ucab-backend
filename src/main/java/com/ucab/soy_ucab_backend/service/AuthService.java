package com.ucab.soy_ucab_backend.service;


import com.ucab.soy_ucab_backend.dto.auth.*;
import com.ucab.soy_ucab_backend.model.Miembro;
import com.ucab.soy_ucab_backend.model.Organizacion;

import com.ucab.soy_ucab_backend.model.Persona;
import com.ucab.soy_ucab_backend.model.PeriodoEducativo;
import com.ucab.soy_ucab_backend.model.PeriodoExperiencia;
import com.ucab.soy_ucab_backend.repository.MiembroRepository;
import com.ucab.soy_ucab_backend.repository.OrganizacionRepository;
import com.ucab.soy_ucab_backend.repository.PersonaRepository;
import com.ucab.soy_ucab_backend.repository.PeriodoEducativoRepository;
import com.ucab.soy_ucab_backend.repository.PeriodoExperienciaRepository;

import com.ucab.soy_ucab_backend.repository.CarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
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
    private PeriodoEducativoRepository periodoEducativoRepository;

    @Autowired
    private PeriodoExperienciaRepository periodoExperienciaRepository;

    @Autowired
    private CarreraRepository carreraRepository;

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
        return buildAuthResponse(miembro);
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

        return new AuthResponseDto(persona.getEmail(), persona.getRole().name(), "Persona", persona, 0, 0L, null, null, null, null, persona.getLocation(), null);
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

        return buildAuthResponse(org);
    }

    public AuthResponseDto buildAuthResponse(Miembro miembro) {
        long followersCount = 0; // segueRepository.countByCorreoSeguido(miembro.getEmail()); 
        Long friendsCount = null;
        String profileImage = null;
        String memberType = miembro instanceof Persona ? "Persona" : (miembro instanceof Organizacion ? "Organizacion" : "Miembro");

        if (miembro.getArchivoFoto() != null && miembro.getFormatoFoto() != null) {
            String base64Info = java.util.Base64.getEncoder().encodeToString(miembro.getArchivoFoto());
            profileImage = "data:image/" + miembro.getFormatoFoto() + ";base64," + base64Info;
        }

        List<PeriodoEducativo> academicPeriods = null;
        List<PeriodoExperiencia> professionalPeriods = null;
        String location = null;

        if (miembro instanceof Persona) {
            academicPeriods = periodoEducativoRepository.findByCorreoPersona(miembro.getEmail());
            if (academicPeriods != null) {
                for (PeriodoEducativo pe : academicPeriods) {
                    if (pe.getIdCarrera() != null) {
                        carreraRepository.findById(pe.getIdCarrera()).ifPresent(carrera -> {
                            if (carrera.getNivelCarrera() != null) {
                                pe.setCareerLevel(carrera.getNivelCarrera().toString());
                            }
                            if (carrera.getCorreoEscuela() != null) {
                                organizacionRepository.findById(carrera.getCorreoEscuela()).ifPresent(org -> {
                                    pe.setSchoolName(org.getName());
                                });
                            }
                        });
                    }
                }
            }
            professionalPeriods = periodoExperienciaRepository.findByCorreoPersona(miembro.getEmail());
            location = ((Persona) miembro).getLocation();
        }

        return new AuthResponseDto(miembro.getEmail(), miembro.getRole().name(), memberType, miembro, followersCount, friendsCount, profileImage, miembro.getEncabezadoPerfil(), academicPeriods, professionalPeriods, location, miembro.getInterests());
    }
}
