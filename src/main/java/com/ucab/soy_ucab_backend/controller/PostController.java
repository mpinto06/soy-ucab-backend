package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.dto.CreatePostDto;
import com.ucab.soy_ucab_backend.service.PostService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public void createPost(
            @RequestParam String userId,
            @RequestBody CreatePostDto dto
    ) {
        postService.createPost(userId, dto);
    }

    @PostMapping("/vote")
    public ResponseEntity<Void> vote(
            @RequestParam String userId,
            @RequestBody com.ucab.soy_ucab_backend.dto.VoteDto voteDto
    ) {
        // We use the passed userId instead of context to be safe/consistent with createPost 
        // OR we can trust context. But user specifically asked for userId param pattern.
        // Actually earlier code used context: String userId = SecurityContextHolder...
        // I will allow the param to override or be the primary source.
        postService.voteInSurvey(userId, voteDto.getPostId(), voteDto.getPostAuthorId(), voteDto.getOptionId());
        return ResponseEntity.ok().build();
    }
}
