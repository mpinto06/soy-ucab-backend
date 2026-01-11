package com.ucab.soy_ucab_backend.service;

import com.ucab.soy_ucab_backend.model.Like;
import com.ucab.soy_ucab_backend.model.LikeId;
import com.ucab.soy_ucab_backend.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    public boolean toggleLike(String userId, String postId, String postAuthorId) {
        LikeId id = new LikeId(userId, postId, postAuthorId);
        Optional<Like> existingLike = likeRepository.findById(id);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            // Trigger will decrease count
            return false; // Not liked anymore
        } else {
            Like newLike = new Like(userId, postId, postAuthorId);
            likeRepository.save(newLike);
            // Trigger will increase count
            return true; // Liked
        }
    }
}
