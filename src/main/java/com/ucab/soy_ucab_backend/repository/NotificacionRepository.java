package com.ucab.soy_ucab_backend.repository;

import com.ucab.soy_ucab_backend.model.Notificacion;
import com.ucab.soy_ucab_backend.model.NotificacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, NotificacionId> {
    List<Notificacion> findByCorreoDestinatarioOrderByFechaHoraDesc(String correo);
}
