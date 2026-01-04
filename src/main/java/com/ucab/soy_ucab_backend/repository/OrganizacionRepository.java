package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Organizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizacionRepository extends JpaRepository<Organizacion, String> {
}
