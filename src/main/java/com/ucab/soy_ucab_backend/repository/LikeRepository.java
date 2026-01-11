package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Like;
import com.ucab.soy_ucab_backend.model.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId> {
}
