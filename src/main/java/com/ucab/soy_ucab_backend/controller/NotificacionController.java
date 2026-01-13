package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.dto.NotificacionDto;
import com.ucab.soy_ucab_backend.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping
    public List<NotificacionDto> getNotifications(@RequestParam String email) {
        return notificacionService.getUserNotifications(email);
    }

    @PatchMapping("/{id:.+}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id, @RequestParam String email) {
        notificacionService.markAsRead(id, email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@RequestParam String email) {
        notificacionService.markAllAsRead(email);
        return ResponseEntity.ok().build();
    }
}
