package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Sigue;
import com.ucab.soy_ucab_backend.model.SigueId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SigueRepository extends JpaRepository<Sigue, SigueId> {
    long countByFollowedEmail(String followedEmail);
}
