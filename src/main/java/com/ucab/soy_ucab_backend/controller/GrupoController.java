package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.dto.GrupoDto;
import com.ucab.soy_ucab_backend.dto.PublicacionDto;
import com.ucab.soy_ucab_backend.model.Grupo;
import com.ucab.soy_ucab_backend.model.Miembro;
import com.ucab.soy_ucab_backend.repository.MiembroRepository;
import com.ucab.soy_ucab_backend.repository.PerteneceRepository;
import com.ucab.soy_ucab_backend.repository.PublicacionRepository;
import com.ucab.soy_ucab_backend.service.GrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/groups")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private MiembroRepository miembroRepository;

    @Autowired
    private PerteneceRepository perteneceRepository;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @GetMapping
    public List<GrupoDto> getAllGroups(@RequestParam(required = false) String requesterId) {
        return grupoService.findAll().stream()
                .map(g -> convertToDto(g, requesterId))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoDto> getGroupById(@PathVariable String id,
            @RequestParam(required = false) String requesterId) {
        return grupoService.findById(id)
                .map(g -> convertToDto(g, requesterId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody GrupoDto dto) {
        if (dto.getNombreGrupo() == null || dto.getCorreoCreador() == null) {
            return ResponseEntity.badRequest().body("nombreGrupo and correoCreador are required");
        }

        if (grupoService.findById(dto.getNombreGrupo()).isPresent()) {
            return ResponseEntity.badRequest().body("Group already exists with this name");
        }

        Miembro creator = miembroRepository.findById(dto.getCorreoCreador()).orElse(null);
        if (creator == null) {
            return ResponseEntity.badRequest().body("Creator not found: " + dto.getCorreoCreador());
        }

        Grupo grupo = convertToEntity(dto, creator);
        grupo.setFechaCreacion(LocalDate.now()); // Set implementation date
        Grupo saved = grupoService.save(grupo);
        return ResponseEntity.ok(convertToDto(saved, dto.getCorreoCreador()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroup(@PathVariable String id, @RequestBody GrupoDto dto) {
        return grupoService.findById(id).map(existing -> {
            existing.setDescripcion(dto.getDescription());
            existing.setTipoGrupo(dto.getTipoGrupo());

            // Note: Not updating creator or name as they are critical fields

            Grupo updated = grupoService.save(existing);
            // Use the original creator's email to ensure the response still marks the
            // requester as owner if they were the owner
            return ResponseEntity.ok(convertToDto(updated, existing.getCreador().getEmail()));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable String id) {
        if (grupoService.findById(id).isPresent()) {
            grupoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private GrupoDto convertToDto(Grupo grupo, String requesterId) {
        GrupoDto dto = new GrupoDto();
        dto.setNombreGrupo(grupo.getNombreGrupo());
        dto.setDescription(grupo.getDescripcion());
        dto.setTipoGrupo(grupo.getTipoGrupo());
        dto.setFechaCreacion(grupo.getFechaCreacion());
        dto.setCorreoCreador(grupo.getCreador().getEmail());

        if (requesterId != null) {
            dto.setCreator(grupo.getCreador().getEmail().equalsIgnoreCase(requesterId));
        } else {
            dto.setCreator(false);
        }

        dto.setMemberCount((int) perteneceRepository.countByNombreGrupo(grupo.getNombreGrupo()));

        dto.setPosts(publicacionRepository.findByIdGrupoOrderByFechaHoraDesc(grupo.getNombreGrupo()).stream()
                .map(this::convertToPublicacionDto)
                .collect(Collectors.toList()));

        // Note: weeklyPosts/monthlyPosts could also be calculated here, but setting to
        // 0 for now
        return dto;
    }

    private PublicacionDto convertToPublicacionDto(com.ucab.soy_ucab_backend.model.Publicacion p) {
        PublicacionDto dto = new PublicacionDto();
        dto.setIdPublicacion(p.getIdPublicacion());
        dto.setCorreoAutor(p.getCorreoAutor());
        dto.setTextoPub(p.getTextoPub());
        dto.setFechaHora(p.getFechaHora());
        dto.setTotalLikes(p.getTotalLikes());
        dto.setTotalComen(p.getTotalComen());
        return dto;
    }

    private Grupo convertToEntity(GrupoDto dto, Miembro creator) {
        Grupo grupo = new Grupo();
        grupo.setNombreGrupo(dto.getNombreGrupo());
        grupo.setDescripcion(dto.getDescription());
        grupo.setTipoGrupo(dto.getTipoGrupo());
        grupo.setCreador(creator);
        // fechaCreacion handled in service
        return grupo;
    }
}
