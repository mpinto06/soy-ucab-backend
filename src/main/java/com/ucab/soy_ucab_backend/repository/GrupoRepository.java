package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, String> {
    java.util.List<Grupo> findByCreador_Email(String email);
}
