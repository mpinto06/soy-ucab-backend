package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.dto.EventoDto;
import com.ucab.soy_ucab_backend.model.Evento;
import com.ucab.soy_ucab_backend.model.Miembro;
import com.ucab.soy_ucab_backend.repository.MiembroRepository;
import com.ucab.soy_ucab_backend.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private MiembroRepository miembroRepository;

    @GetMapping
    public List<EventoDto> getAllEvents() {
        return eventoService.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoDto> getEventById(@PathVariable String id) {
        return eventoService.findById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody EventoDto dto) {
        if (dto.getNombreEvento() == null || dto.getCorreoOrganizador() == null) {
            return ResponseEntity.badRequest().body("nombre_evento and correo_organizador are required");
        }

        Miembro organizer = miembroRepository.findById(dto.getCorreoOrganizador()).orElse(null);
        if (organizer == null) {
            return ResponseEntity.badRequest().body("Organizer not found: " + dto.getCorreoOrganizador());
        }

        Evento evento = convertToEntity(dto, organizer);
        Evento saved = eventoService.save(evento);
        return ResponseEntity.ok(convertToDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable String id, @RequestBody EventoDto dto) {
        return eventoService.findById(id).map(existing -> {
            Miembro organizer = existing.getOrganizador();
            if (dto.getCorreoOrganizador() != null
                    && !dto.getCorreoOrganizador().equals(organizer.getEmail())) {
                Miembro newOrganizer = miembroRepository.findById(dto.getCorreoOrganizador()).orElse(null);
                if (newOrganizer != null)
                    organizer = newOrganizer;
            }

            existing.setDescripcion(dto.getDescripcion());
            existing.setModalidad(dto.getModalidad());
            existing.setFechaInicio(dto.getFechaInicio());
            existing.setFechaFin(dto.getFechaFin());
            existing.setUbicacion(dto.getUbicacion());
            existing.setEstadoEvento(dto.getEstadoEvento());
            existing.setUrlConferencia(dto.getUrlConferencia());
            existing.setOrganizador(organizer);

            Evento updated = eventoService.save(existing);
            return ResponseEntity.ok(convertToDto(updated));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        if (eventoService.findById(id).isPresent()) {
            eventoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private EventoDto convertToDto(Evento evento) {
        EventoDto dto = new EventoDto();
        dto.setNombreEvento(evento.getNombreEvento());
        dto.setCorreoOrganizador(evento.getOrganizador().getEmail());
        dto.setDescripcion(evento.getDescripcion());
        dto.setModalidad(evento.getModalidad());
        dto.setFechaInicio(evento.getFechaInicio());
        dto.setFechaFin(evento.getFechaFin());
        dto.setUbicacion(evento.getUbicacion());
        dto.setEstadoEvento(evento.getEstadoEvento());
        dto.setUrlConferencia(evento.getUrlConferencia());
        return dto;
    }

    private Evento convertToEntity(EventoDto dto, Miembro organizer) {
        Evento evento = new Evento();
        evento.setNombreEvento(dto.getNombreEvento());
        evento.setOrganizador(organizer);
        evento.setDescripcion(dto.getDescripcion());
        evento.setModalidad(dto.getModalidad());
        evento.setFechaInicio(dto.getFechaInicio());
        evento.setFechaFin(dto.getFechaFin());
        evento.setUbicacion(dto.getUbicacion());
        evento.setEstadoEvento(dto.getEstadoEvento());
        evento.setUrlConferencia(dto.getUrlConferencia());
        return evento;
    }
}
