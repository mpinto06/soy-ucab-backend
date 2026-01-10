package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.PeriodoExperiencia;
import com.ucab.soy_ucab_backend.model.PeriodoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeriodoExperienciaRepository extends JpaRepository<PeriodoExperiencia, PeriodoId> {
    List<PeriodoExperiencia> findByCorreoPersona(String correoPersona);
    java.util.Optional<PeriodoExperiencia> findByCorreoPersonaAndIdPeriodo(String correoPersona, String idPeriodo);
}
