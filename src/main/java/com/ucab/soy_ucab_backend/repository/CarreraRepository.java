package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, String> {
}
