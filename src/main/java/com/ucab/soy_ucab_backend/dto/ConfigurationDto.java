package com.ucab.soy_ucab_backend.dto;

public record ConfigurationDto(
    String messagePrivacy,
    boolean posts,
    boolean interactions,
    boolean jobs,
    boolean events,
    boolean newFollowers,
    boolean newFriends
) {}
