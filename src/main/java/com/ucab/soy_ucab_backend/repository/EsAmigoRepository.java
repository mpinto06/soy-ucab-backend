package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.EsAmigo;
import com.ucab.soy_ucab_backend.model.EsAmigoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EsAmigoRepository extends JpaRepository<EsAmigo, EsAmigoId> {
    
    @Query("SELECT COUNT(e) FROM EsAmigo e WHERE (e.person1Email = ?1 OR e.person2Email = ?1) AND e.status = 'aceptada'")
    long countFriendsByEmail(String email);
}
