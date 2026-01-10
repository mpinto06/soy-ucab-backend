package com.ucab.soy_ucab_backend.dto;

import java.time.Instant;

public class FeedPostDto {
    private String id;
    private AuthorDto author;
    private String group;
    private String content;
    private Instant date;
    private int likes;
    private int comments;

    // Inner DTO for Author to structure the JSON nicely
    public static class AuthorDto {
        private String id;
        private String name;
        private String photoUrl; // We will send Base64 here or logic to fetch it? For now assume Base64 in string or just "photo" field
        // Actually, user said include photo. Sending byte array in JSON is heavy but maybe what's expected if small.
        // Or better: Base64 string.
        private String photoBase64;
        private String headline;

        public AuthorDto(String id, String name, String photoBase64, String headline) {
            this.id = id;
            this.name = name;
            this.photoBase64 = photoBase64;
            this.headline = headline;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getPhotoBase64() { return photoBase64; }
        public String getHeadline() { return headline; }
    }

    public FeedPostDto(String id, AuthorDto author, String group, String content, Instant date, int likes, int comments) {
        this.id = id;
        this.author = author;
        this.group = group;
        this.content = content;
        this.date = date;
        this.likes = likes;
        this.comments = comments;
    }

    // Getters
    public String getId() { return id; }
    public AuthorDto getAuthor() { return author; }
    public String getGroup() { return group; }
    public String getContent() { return content; }
    public Instant getDate() { return date; }
    public int getLikes() { return likes; }
    public int getComments() { return comments; }
}
