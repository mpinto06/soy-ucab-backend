package com.ucab.soy_ucab_backend.service;

import com.ucab.soy_ucab_backend.dto.FeedPostDto;
import com.ucab.soy_ucab_backend.dto.FeedPostProjection;
import com.ucab.soy_ucab_backend.dto.FeedResponseDto;
import com.ucab.soy_ucab_backend.repository.FeedRepository;
import com.ucab.soy_ucab_backend.repository.EncuestaRepository;
import com.ucab.soy_ucab_backend.repository.OpcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
public class FeedService {

    @Autowired
    private FeedRepository feedRepository;
    
    @Autowired
    private EncuestaRepository encuestaRepository;
    
    @Autowired
    private OpcionRepository opcionRepository;
    
    @Autowired
    private com.ucab.soy_ucab_backend.repository.ArchivoPublicacionRepository archivoPublicacionRepository;

    public FeedResponseDto getFeed(
            String email,
            int page,
            int pageSize,
            String search,
            boolean filterInterests,
            boolean filterFriends,
            boolean filterFollowing,
            boolean filterGroups,
            boolean filterOwnPosts,
            String order
    ) {
        boolean orderAsc = "asc".equalsIgnoreCase(order);
        
        List<FeedPostProjection> projections = feedRepository.getFeed(
                email,
                page,
                pageSize,
                search,
                filterInterests,
                filterFriends,
                filterFollowing,
                filterGroups,
                filterOwnPosts,
                orderAsc
        );

        List<FeedPostDto> posts = projections.stream().map(p -> {
            String photoBase64 = null;
            if (p.getAutor_foto() != null) {
                photoBase64 = "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(p.getAutor_foto());
                // Assuming JPEG or generic logic. The database has "extension_imagen" enum but we might assume standard or just send raw base64 and frontend adds prefix.
                // Keeping it simple: Send URI scheme if possible, or just base64. 
                // Let's rely on frontend to handle or add prefix if needed. 
                // Wait, User profile usually needs "data:image/..."
                // I will add the prefix if it's missing in frontend logic, but here let's valid Base64 string.
            }

            List<String> interests = new ArrayList<>();
            if (p.getIntereses() != null && !p.getIntereses().isEmpty()) {
                interests = Arrays.asList(p.getIntereses().split(", "));
            }

            List<FeedPostDto.FileMetaDto> files = new ArrayList<>();
            // Check existence first using the aggregated string (optimization to avoid query if empty)
            if (p.getArchivos_str() != null && !p.getArchivos_str().isEmpty()) {
                 List<com.ucab.soy_ucab_backend.model.ArchivoPublicacion> dbFiles = archivoPublicacionRepository.findByIdPublicacionAndCorreoAutor(p.getId_pub(), p.getAutor_id());
                 for (com.ucab.soy_ucab_backend.model.ArchivoPublicacion file : dbFiles) {
                     String base64 = "";
                     if (file.getArchivo() != null) {
                         // Determine mime type based on format
                         String mimeType = "application/octet-stream";
                         String fmt = file.getFormatoArchivo().toLowerCase();
                         if (fmt.equals("jpg") || fmt.equals("jpeg")) mimeType = "image/jpeg";
                         else if (fmt.equals("png")) mimeType = "image/png";
                         else if (fmt.equals("mp4")) mimeType = "video/mp4";
                         
                         base64 = "data:" + mimeType + ";base64," + java.util.Base64.getEncoder().encodeToString(file.getArchivo());
                     }
                     files.add(new FeedPostDto.FileMetaDto(file.getNombreArchivo(), file.getFormatoArchivo(), base64));
                 }
            }
            
            FeedPostDto.PollDto poll = null;
            if (Boolean.TRUE.equals(p.getTiene_encuesta())) {
                com.ucab.soy_ucab_backend.model.Encuesta encuesta = encuestaRepository.findByIdPublicacionAndCorreoAutor(p.getId_pub(), p.getAutor_id()).orElse(null);
                if (encuesta != null) {
                   List<com.ucab.soy_ucab_backend.model.Opcion> opciones = opcionRepository.findByIdPublicacionAndCorreoAutor(p.getId_pub(), p.getAutor_id());
                   List<FeedPostDto.OptionDto> optionsDto = opciones.stream()
                        .map(o -> new FeedPostDto.OptionDto(o.getTextoOpcion(), o.getTotalVotos()))
                        .toList();
                   poll = new FeedPostDto.PollDto(encuesta.getFechaHoraFin().toString(), optionsDto, p.getVotoUsuario());
                }
            }

            return new FeedPostDto(
                p.getId_pub(),
                new FeedPostDto.AuthorDto(
                    p.getAutor_id(),
                    p.getAutor_nombre(),
                    photoBase64,
                    p.getAutor_encabezado()
                ),
                p.getGrupo(),
                p.getTexto(),
                p.getFecha(),
                p.getLikes(),
                p.getComentarios(),
                Boolean.TRUE.equals(p.getMi_like()),
                interests,
                poll,
                files
            );
        }).toList();

        long totalRecords = 0;
        if (!projections.isEmpty()) {
            totalRecords = projections.get(0).getTotal_registros();
        }

        long totalPages = (long) Math.ceil((double) totalRecords / pageSize);
        boolean hasMorePages = page < totalPages;

        return new FeedResponseDto(posts, totalRecords, hasMorePages);
    }
}
