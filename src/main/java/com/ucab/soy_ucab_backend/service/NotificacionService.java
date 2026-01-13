package com.ucab.soy_ucab_backend.service;

import com.ucab.soy_ucab_backend.dto.NotificacionDto;
import com.ucab.soy_ucab_backend.model.Notificacion;
import com.ucab.soy_ucab_backend.model.NotificacionId;
import com.ucab.soy_ucab_backend.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionService {
    @Autowired
    private NotificacionRepository notificacionRepository;

    public List<NotificacionDto> getUserNotifications(String email) {
        return notificacionRepository.findByCorreoDestinatarioOrderByFechaHoraDesc(email)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(String id, String email) {
        notificacionRepository.findById(new NotificacionId(id, email)).ifPresent(notif -> {
            notif.setLeida(true);
            notificacionRepository.save(notif);
        });
    }

    @Transactional
    public void markAllAsRead(String email) {
        List<Notificacion> unread = notificacionRepository.findByCorreoDestinatarioOrderByFechaHoraDesc(email)
                .stream()
                .filter(n -> !n.getLeida())
                .collect(Collectors.toList());

        unread.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(unread);
    }

    private NotificacionDto convertToDto(Notificacion n) {
        NotificacionDto dto = new NotificacionDto();
        dto.setId(n.getIdNotificacion());
        dto.setAction(n.getTextoNotificacion());
        dto.setTimestamp(n.getFechaHora());
        dto.setRead(n.getLeida() != null && n.getLeida());

        return dto;
    }
}
