package com.ucab.soy_ucab_backend.dto;

public class CreateCommentDto {
    private String postId;
    private String postAuthorId;
    private String content;

    public CreateCommentDto() {
    }

    public CreateCommentDto(String postId, String postAuthorId, String content) {
        this.postId = postId;
        this.postAuthorId = postAuthorId;
        this.content = content;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostAuthorId() {
        return postAuthorId;
    }

    public void setPostAuthorId(String postAuthorId) {
        this.postAuthorId = postAuthorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
