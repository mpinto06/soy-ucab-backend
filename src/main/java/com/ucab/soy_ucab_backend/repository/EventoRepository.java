package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, String> {
}
