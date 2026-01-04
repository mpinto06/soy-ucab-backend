package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MiembroRepository extends JpaRepository<Miembro, String> {
    Optional<Miembro> findByEmail(String email);
}
