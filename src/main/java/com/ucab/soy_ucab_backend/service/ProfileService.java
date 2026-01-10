package com.ucab.soy_ucab_backend.service;

import com.ucab.soy_ucab_backend.dto.PeriodDto;
import com.ucab.soy_ucab_backend.dto.auth.AuthResponseDto;
import com.ucab.soy_ucab_backend.dto.auth.UpdateUserRequestDto;
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
import com.ucab.soy_ucab_backend.repository.InteresRepository;
import com.ucab.soy_ucab_backend.model.Interes;
import com.ucab.soy_ucab_backend.repository.HabilidadRepository;
import com.ucab.soy_ucab_backend.model.Habilidad;
import com.ucab.soy_ucab_backend.repository.CarreraRepository;
import com.ucab.soy_ucab_backend.model.Carrera;
import com.ucab.soy_ucab_backend.dto.OrganizationSummaryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProfileService {

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
    private InteresRepository interesRepository;

    @Autowired
    private HabilidadRepository habilidadRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private AuthService authService;

    @org.springframework.transaction.annotation.Transactional
    public AuthResponseDto updateUser(String email, UpdateUserRequestDto request) {
        // 1. Handle Email Update if requested
        String currentEmail = email;
        if (request.newEmail() != null && !request.newEmail().isBlank() && !request.newEmail().equals(currentEmail)) {
            // Validate email format
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if (!request.newEmail().matches(emailRegex)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de correo electrónico inválido");
            }

            // Call stored procedure
            miembroRepository.actualizarCorreoSeguro(currentEmail, request.newEmail());
            // Update currentEmail reference for subsequent lookups/updates
            currentEmail = request.newEmail();
        }

        Miembro miembro = miembroRepository.findById(currentEmail).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        
        if (request.profileImageBase64() != null) {
            try {
                String b64 = request.profileImageBase64();
                if (b64.contains(",")) b64 = b64.split(",")[1];
                miembro.setArchivoFoto(java.util.Base64.getDecoder().decode(b64));
                
                // Set format and name if provided, otherwise default or derive
                if (request.imageFormat() != null) {
                     miembro.setFormatoFoto(request.imageFormat());
                } else {
                     miembro.setFormatoFoto("png"); // Fallback
                }

                if (request.imageName() != null) {
                    miembro.setNombreArchivoFoto(request.imageName());
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        if (request.profileHeader() != null) miembro.setEncabezadoPerfil(request.profileHeader());


        if (miembro instanceof Persona) {
             Persona p = (Persona) miembro;
             if (request.firstName() != null) p.setFirstName(request.firstName());
             if (request.lastName() != null) p.setLastName(request.lastName());
             if (request.gender() != null) p.setGender(request.gender());
             if (request.location() != null) p.setLocation(request.location());
             personaRepository.save(p);
        } else if (miembro instanceof Organizacion) {
            Organizacion o = (Organizacion) miembro;
            if (request.firstName() != null) o.setName(request.firstName());
            organizacionRepository.save(o);
        } else {
             miembroRepository.save(miembro);
        }

        return authService.buildAuthResponse(miembro);
    }

    public AuthResponseDto addInterest(String email, String interest) {
        // Ensure interest exists in master table
        if (!interesRepository.existsById(interest)) {
            interesRepository.save(new Interes(interest));
        }

        Miembro miembro = miembroRepository.findById(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (miembro.getInterests() == null) miembro.setInterests(new java.util.ArrayList<>());
        if (!miembro.getInterests().contains(interest)) {
            miembro.getInterests().add(interest);
            miembroRepository.save(miembro);
        }
        return authService.buildAuthResponse(miembro);
    }

    public AuthResponseDto removeInterest(String email, String interest) {
        Miembro miembro = miembroRepository.findById(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (miembro.getInterests() != null) {
            miembro.getInterests().remove(interest);
            miembroRepository.save(miembro);
        }
        return authService.buildAuthResponse(miembro);
    }

    public List<String> getAllInterests() {
        return interesRepository.findAll().stream().map(Interes::getNombreInteres).toList();
    }

    public List<String> getAllSkills() {
        return habilidadRepository.findAll().stream().map(Habilidad::getNombreHabilidad).toList();
    }

    public List<OrganizationSummaryDto> getAllOrganizations() {
        return organizacionRepository.findAll().stream()
                .map(o -> new OrganizationSummaryDto(o.getEmail(), o.getName()))
                .toList();
    }

    public List<String> getAllCareers() {
        return carreraRepository.findAll().stream().map(Carrera::getNombreCarrera).toList();
    }

    public AuthResponseDto savePeriod(String email, PeriodDto periodDto) {
        if (!miembroRepository.existsById(email)) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        // Ensure all skills exist
        if (periodDto.skills() != null) {
            for (String skill : periodDto.skills()) {
                if (!habilidadRepository.existsById(skill)) {
                    habilidadRepository.save(new Habilidad(skill));
                }
            }
        }

        String newId = periodDto.id();
        if (newId == null) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            newId = java.time.LocalDateTime.now().format(formatter);
        }

        if ("academic".equalsIgnoreCase(periodDto.type())) {
            PeriodoEducativo p = new PeriodoEducativo();
            p.setIdPeriodo(newId);
            p.setCorreoPersona(email);
            p.setFechaInicio(periodDto.startDate());
            p.setFechaFin(periodDto.endDate());
            p.setDescripcionPeriodo(periodDto.description());
            p.setSkills(periodDto.skills());
            // Academic specific
            String vidCarrera = periodDto.degreeId();
            if (vidCarrera != null && vidCarrera.isBlank()) vidCarrera = null;
            p.setIdCarrera(vidCarrera);

            String vNombreEstudio = periodDto.title();
            if (vNombreEstudio != null && vNombreEstudio.isBlank()) vNombreEstudio = null;
            p.setNombreEstudio(vNombreEstudio);
            
            periodoEducativoRepository.save(p);
        } else if ("professional".equalsIgnoreCase(periodDto.type())) {
            PeriodoExperiencia p = new PeriodoExperiencia();
            p.setIdPeriodo(newId);
            p.setCorreoPersona(email);
            p.setFechaInicio(periodDto.startDate());
            p.setFechaFin(periodDto.endDate());
            p.setDescripcionPeriodo(periodDto.description());
            p.setSkills(periodDto.skills());
            // Professional specific
            p.setCorreoOrganizacion(periodDto.organization()); // Assuming org is passed as string identifier or name
            p.setTipoCargo(periodDto.positionType());
            p.setCargo(periodDto.position());

            periodoExperienciaRepository.save(p);
        } else {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de periodo inválido");
        }

        return authService.buildAuthResponse(miembroRepository.findById(email).orElseThrow());
    }

    @org.springframework.transaction.annotation.Transactional
    public AuthResponseDto deletePeriod(String email, String periodId, String type) {
        if ("academic".equalsIgnoreCase(type)) {
            PeriodoEducativo p = periodoEducativoRepository.findByCorreoPersonaAndIdPeriodo(email, periodId).orElse(null);
            if (p != null) {
                if (p.getSkills() != null) {
                    p.getSkills().clear();
                    periodoEducativoRepository.save(p);
                    periodoEducativoRepository.flush();
                }
                periodoEducativoRepository.delete(p);
            }
        } else if ("professional".equalsIgnoreCase(type)) {
             PeriodoExperiencia p = periodoExperienciaRepository.findByCorreoPersonaAndIdPeriodo(email, periodId).orElse(null);
             if (p != null) {
                 if (p.getSkills() != null) {
                     p.getSkills().clear();
                     periodoExperienciaRepository.save(p);
                     periodoExperienciaRepository.flush();
                 }
                 periodoExperienciaRepository.delete(p);
            }
        }
        
        return authService.buildAuthResponse(miembroRepository.findById(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")));
    }
}
