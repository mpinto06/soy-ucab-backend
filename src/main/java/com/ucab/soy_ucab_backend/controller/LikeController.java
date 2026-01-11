package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleLike(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        String postId = payload.get("postId");
        String postAuthorId = payload.get("postAuthorId");

        if (userId == null || postId == null || postAuthorId == null) {
            return ResponseEntity.badRequest().body("userId, postId, and postAuthorId are required");
        }

        boolean isLiked = likeService.toggleLike(userId, postId, postAuthorId);
        return ResponseEntity.ok(Map.of("liked", isLiked));
    }
}
