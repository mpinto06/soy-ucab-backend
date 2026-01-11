package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, String> {
    @Query("SELECT g FROM Grupo g JOIN Pertenece p ON g.nombre = p.nombreGrupo WHERE p.correoMiembro = :email")
    List<Grupo> findGroupsByMemberEmail(@Param("email") String email);
}
