package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Opcion;
import com.ucab.soy_ucab_backend.model.OpcionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpcionRepository extends JpaRepository<Opcion, OpcionId> {
    List<Opcion> findByIdPublicacionAndCorreoAutor(String idPublicacion, String correoAutor);
}
