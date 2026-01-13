package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.CatalogoOficial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogoOficialRepository extends JpaRepository<CatalogoOficial, Long> {

    @Query(value = """
            SELECT c.nombre_escuela, c.nombre_facultad
            FROM Catalogo_Oficial_UCAB c
            WHERE c.nombre_escuela NOT IN (
                SELECT o.nombre_organizacion
                FROM Organizacion o
                JOIN Dependencia_UCAB d ON o.correo_electronico = d.correo_electronico
                WHERE d.tipo_entidad_institucional = 'escuela'
            )
            """, nativeQuery = true)
    List<Object[]> findUnregisteredSchools();

    @Query(value = """
            SELECT DISTINCT c.nombre_facultad
            FROM Catalogo_Oficial_UCAB c
            WHERE c.nombre_facultad NOT IN (
                SELECT o.nombre_organizacion
                FROM Organizacion o
                JOIN Dependencia_UCAB d ON o.correo_electronico = d.correo_electronico
                WHERE d.tipo_entidad_institucional = 'facultad'
            )
            """, nativeQuery = true)
    List<String> findUnregisteredFaculties();

    CatalogoOficial findByNombreEscuela(String nombreEscuela);
}
