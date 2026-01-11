package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Encuesta;
import com.ucab.soy_ucab_backend.model.EncuestaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EncuestaRepository extends JpaRepository<Encuesta, EncuestaId> {
    Optional<Encuesta> findByIdPublicacionAndCorreoAutor(String idPublicacion, String correoAutor);
}
