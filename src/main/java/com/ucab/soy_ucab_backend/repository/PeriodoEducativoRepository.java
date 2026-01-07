package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.PeriodoEducativo;
import com.ucab.soy_ucab_backend.model.PeriodoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeriodoEducativoRepository extends JpaRepository<PeriodoEducativo, PeriodoId> {
    List<PeriodoEducativo> findByCorreoPersona(String correoPersona);
}
