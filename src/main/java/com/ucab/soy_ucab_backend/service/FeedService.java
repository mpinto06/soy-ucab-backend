package com.ucab.soy_ucab_backend.service;

import com.ucab.soy_ucab_backend.dto.FeedPostDto;
import com.ucab.soy_ucab_backend.dto.FeedPostProjection;
import com.ucab.soy_ucab_backend.dto.FeedResponseDto;
import com.ucab.soy_ucab_backend.repository.FeedRepository;
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
                interests
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
