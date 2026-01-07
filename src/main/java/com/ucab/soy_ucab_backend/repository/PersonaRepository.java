package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, String> {
    @org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM Es_Amigo WHERE (correo_persona1 = :email OR correo_persona2 = :email) AND estado = 'aceptada'", nativeQuery = true)
    long countFriends(@org.springframework.data.repository.query.Param("email") String email);
}
