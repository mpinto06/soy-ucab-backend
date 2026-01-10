package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.dto.FeedResponseDto;
import com.ucab.soy_ucab_backend.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @GetMapping
    public FeedResponseDto getFeed(
            @RequestParam String email,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "true") boolean interests,
            @RequestParam(defaultValue = "true") boolean friends,
            @RequestParam(defaultValue = "true") boolean following,
            @RequestParam(defaultValue = "true") boolean groups,
            @RequestParam(defaultValue = "true") boolean ownPosts,
            @RequestParam(defaultValue = "desc") String order
    ) {
        return feedService.getFeed(
                email,
                page,
                pageSize,
                search,
                interests,
                friends,
                following,
                groups,
                ownPosts,
                order
        );
    }
}
