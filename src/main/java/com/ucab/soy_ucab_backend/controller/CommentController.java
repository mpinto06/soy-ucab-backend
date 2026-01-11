package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.dto.CommentDto;
import com.ucab.soy_ucab_backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public List<CommentDto> getComments(
            @RequestParam String postId,
            @RequestParam String postAuthorId
    ) {
        return commentService.getComments(postId, postAuthorId);
    }
}
