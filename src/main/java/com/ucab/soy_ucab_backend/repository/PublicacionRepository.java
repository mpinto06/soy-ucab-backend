package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Publicacion;
import com.ucab.soy_ucab_backend.model.PublicacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, PublicacionId> {
    java.util.List<Publicacion> findByIdGrupoOrderByFechaHoraDesc(String idGrupo);
}
