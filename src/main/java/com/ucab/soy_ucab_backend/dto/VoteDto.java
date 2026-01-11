package com.ucab.soy_ucab_backend.dto;

public class VoteDto {
    private String postId;
    private String postAuthorId;
    private String optionId; // This corresponds to the option TEXT

    public VoteDto() {}

    public VoteDto(String postId, String postAuthorId, String optionId) {
        this.postId = postId;
        this.postAuthorId = postAuthorId;
        this.optionId = optionId;
    }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getPostAuthorId() { return postAuthorId; }
    public void setPostAuthorId(String postAuthorId) { this.postAuthorId = postAuthorId; }

    public String getOptionId() { return optionId; }
    public void setOptionId(String optionId) { this.optionId = optionId; }
}
