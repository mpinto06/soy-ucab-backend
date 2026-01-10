package com.ucab.soy_ucab_backend.dto;

import java.util.List;

public record FeedResponseDto(
    List<FeedPostDto> posts,
    long totalRecords,
    boolean hasMorePages
) {}
