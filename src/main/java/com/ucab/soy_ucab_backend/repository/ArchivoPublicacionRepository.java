package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.ArchivoPublicacion;
import com.ucab.soy_ucab_backend.model.ArchivoPublicacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchivoPublicacionRepository extends JpaRepository<ArchivoPublicacion, ArchivoPublicacionId> {
    List<ArchivoPublicacion> findByIdPublicacionAndCorreoAutor(String idPublicacion, String correoAutor);
}
