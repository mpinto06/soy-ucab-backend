package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Pertenece;
import com.ucab.soy_ucab_backend.model.PerteneceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PerteneceRepository extends JpaRepository<Pertenece, PerteneceId> {
    long countByNombreGrupo(String nombreGrupo);

    List<Pertenece> findByNombreGrupo(String nombreGrupo);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Pertenece p WHERE p.nombreGrupo = ?1")
    void deleteByNombreGrupo(String nombreGrupo);
}
