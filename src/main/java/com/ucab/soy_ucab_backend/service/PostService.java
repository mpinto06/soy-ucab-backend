package com.ucab.soy_ucab_backend.service;

import com.ucab.soy_ucab_backend.dto.CreatePostDto;
import com.ucab.soy_ucab_backend.model.*;
import com.ucab.soy_ucab_backend.repository.PublicacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class PostService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PublicacionRepository publicacionRepository;

    public PostService(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    @Transactional
    public void createPost(String userId, CreatePostDto dto) {
        // 1. Create Publicacion
        Publicacion post = new Publicacion();
        // Generate formatted ID
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        String idFormatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // If conflict (unlikely with seconds, but possible in high concurrency), valid option is to append UUID or retry.
        // For this assignment, assuming low concurrency or simple retry manually if needed.
        // Actually, let's just use the timestamp as ID string as DB expects, maybe append random digits if needed?
        // SQL insert showed just timestamp string.
        
        post.setIdPublicacion(idFormatted);
        post.setCorreoAutor(userId);
        post.setIdGrupo(dto.getGroupId()); // Optional group
        post.setTextoPub(dto.getContent());
        post.setFechaHora(now);
        post.setTotalLikes(0);
        post.setTotalComen(0);
        
        post.setTotalComen(0);
        
        // Use entityManager directly to ensure it shares the context and we can flush immediately
        entityManager.persist(post);
        entityManager.flush(); // FORCE flush to ensure 'post' is in the DB before children reference it

        // 2. Handle Files (Archivo_Publicacion)
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            for (CreatePostDto.FileDto fileDto : dto.getFiles()) {
                ArchivoPublicacion file = new ArchivoPublicacion();
                ArchivoPublicacionId id = new ArchivoPublicacionId(post.getIdPublicacion(), userId, fileDto.getName(), fileDto.getFormat());
                file.setId(id);
                file.setPublicacion(post);
                
                // Decode Base64 to byte[]
                try {
                    String base64Clean = cleanBase64(fileDto.base64);
                    byte[] data = Base64.getDecoder().decode(base64Clean);
                    file.setArchivo(data);
                    
                    entityManager.persist(file);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid Base64 for file: " + fileDto.name);
                }
            }
        }

        // 3. Handle Survey (Encuesta + Opcion)
        if (dto.getPoll() != null && dto.getPoll().getOptions() != null && !dto.getPoll().getOptions().isEmpty()) {
            CreatePostDto.PollDto pollDto = dto.getPoll();
            
            Encuesta encuesta = new Encuesta();
            EncuestaId encId = new EncuestaId(post.getIdPublicacion(), userId);
            encuesta.setId(encId);
            encuesta.setPublicacion(post);
            
            if (pollDto.getEndDate() != null) {
                encuesta.setFechaHoraFin(OffsetDateTime.parse(pollDto.getEndDate()));
            }
            
            entityManager.persist(encuesta);

            for (String optionText : pollDto.getOptions()) {
                if (optionText == null || optionText.trim().isEmpty()) continue;
                
                Opcion opcion = new Opcion();
                OpcionId opId = new OpcionId(post.getIdPublicacion(), userId, optionText);
                opcion.setId(opId);
                opcion.setEncuesta(encuesta);
                opcion.setTotalVotos(0);
                
                entityManager.persist(opcion);
            }
        }

        // 4. Handle Interests (Trata_Sobre)
        if (dto.getInterests() != null && !dto.getInterests().isEmpty()) {
            for (String interestName : dto.getInterests()) {
                if (interestName == null || interestName.trim().isEmpty()) continue;
                
                TrataSobre trataSobre = new TrataSobre();
                TrataSobreId tsId = new TrataSobreId(post.getIdPublicacion(), userId, interestName);
                trataSobre.setId(tsId);
                trataSobre.setPublicacion(post);
                // We assume the Interes entity exists. We can set the ID directly or reference it.
                // Setting ID is enough for persist if we don't need to fetch the Interes entity field.
                
                entityManager.persist(trataSobre);
            }
        }
    }
    
    private String cleanBase64(String base64) {
        if (base64.contains(",")) {
            return base64.split(",")[1];
        }
        return base64;
    }

    @Transactional
    public void voteInSurvey(String userId, String postId, String postAuthorId, String optionText) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("registrar_voto_encuesta");
        query.registerStoredProcedureParameter("p_correo_miembro", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_id_publicacion", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_correo_autor_encuesta", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_nueva_opcion", String.class, ParameterMode.IN);

        query.setParameter("p_correo_miembro", userId);
        query.setParameter("p_id_publicacion", postId);
        query.setParameter("p_correo_autor_encuesta", postAuthorId);
        query.setParameter("p_nueva_opcion", optionText);

        query.execute();
    }
}
