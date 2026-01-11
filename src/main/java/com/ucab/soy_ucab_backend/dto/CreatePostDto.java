package com.ucab.soy_ucab_backend.dto;

import java.util.List;

public class CreatePostDto {
    private String content;
    private String groupId; // Optional
    
    private List<String> interests;

    // Media files (Base64) - Max 3 enforced by DB/Frontend, but helpful to validate here too
    private List<FileDto> files;
    
    // Survey
    private PollDto poll;

    public static class FileDto {
        public String name;
        public String format; // jpg, png, mp4
        public String base64;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        public String getBase64() { return base64; }
        public void setBase64(String base64) { this.base64 = base64; }
    }

    public static class PollDto {
        public List<String> options;
        public String endDate; // ISO string for OffsetDateTime (e.g. 2026-01-20T10:00:00Z)

        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
    }

    // Getters and Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }

    public List<FileDto> getFiles() { return files; }
    public void setFiles(List<FileDto> files) { this.files = files; }

    public PollDto getPoll() { return poll; }
    public void setPoll(PollDto poll) { this.poll = poll; }
}
