package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MiembroRepository extends JpaRepository<Miembro, String> {
    Optional<Miembro> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM Sigue WHERE correo_seguido = :email", nativeQuery = true)
    long countFollowers(@org.springframework.data.repository.query.Param("email") String email);

    @org.springframework.data.jpa.repository.query.Procedure(procedureName = "actualizar_correo_seguro")
    void actualizarCorreoSeguro(String p_correo_actual, String p_correo_nuevo);
}
