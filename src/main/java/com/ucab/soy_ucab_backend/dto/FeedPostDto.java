package com.ucab.soy_ucab_backend.dto;

import java.time.Instant;

import java.util.List;
import java.util.ArrayList;

public class FeedPostDto {
    private String id;
    private AuthorDto author;
    private String group;
    private String content;
    private Instant date;
    private int likes;
    private int comments;
    private boolean liked;
    private List<String> interests;
    private PollDto poll;
    private List<FileMetaDto> files;

    // Inner DTO for Author
    public static class AuthorDto {
        private String id;
        private String name;
        private String photoUrl; 
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
    
    // Inner DTO for File Metadata
    public static class FileMetaDto {
        private String name;
        private String format;
        private String url; // Base64 data string

        public FileMetaDto(String name, String format, String url) {
            this.name = name;
            this.format = format;
            this.url = url;
        }

        public String getName() { return name; }
        public String getFormat() { return format; }
        public String getUrl() { return url; }
    }

    // Inner DTO for Poll
    public static class PollDto {
        private String endDate;
        private List<OptionDto> options;
        private String userVotedOption; // New field
        
        public PollDto(String endDate, List<OptionDto> options, String userVotedOption) {
            this.endDate = endDate;
            this.options = options;
            this.userVotedOption = userVotedOption;
        }
        
        public String getEndDate() { return endDate; }
        public List<OptionDto> getOptions() { return options; }
        public String getUserVotedOption() { return userVotedOption; }
    }
    
    // Inner DTO for Poll Option
    public static class OptionDto {
        private String text;
        private Integer votes;
        
        public OptionDto(String text, Integer votes) {
            this.text = text;
            this.votes = votes;
        }
        
        public String getText() { return text; }
        public Integer getVotes() { return votes; }
    }

    public FeedPostDto(String id, AuthorDto author, String group, String content, Instant date, int likes, int comments, boolean liked, List<String> interests, PollDto poll, List<FileMetaDto> files) {
        this.id = id;
        this.author = author;
        this.group = group;
        this.content = content;
        this.date = date;
        this.likes = likes;
        this.comments = comments;
        this.liked = liked;
        this.interests = interests;
        this.poll = poll;
        this.files = files;
    }

    // Getters
    public String getId() { return id; }
    public AuthorDto getAuthor() { return author; }
    public String getGroup() { return group; }
    public String getContent() { return content; }
    public Instant getDate() { return date; }
    public int getLikes() { return likes; }
    public int getComments() { return comments; }
    public boolean isLiked() { return liked; }
    public List<String> getInterests() { return interests; }
    public PollDto getPoll() { return poll; }
    public List<FileMetaDto> getFiles() { return files; }
}
