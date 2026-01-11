package com.ucab.soy_ucab_backend.dto;

import java.time.Instant;

public class CommentDto {
    private String id;
    private String text;
    private Instant date;
    private AuthorDto author;

    public CommentDto(String id, String text, Instant date, AuthorDto author) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.author = author;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getText() { return text; }
    public Instant getDate() { return date; }
    public AuthorDto getAuthor() { return author; }

    public static class AuthorDto {
        private String id;
        private String name;
        private String photoBase64;
        private String headline;

        public AuthorDto(String id, String name, String photoBase64, String headline) {
            this.id = id;
            this.name = name;
            this.photoBase64 = photoBase64;
            this.headline = headline;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getPhotoBase64() { return photoBase64; }
        public String getHeadline() { return headline; }
    }
}
