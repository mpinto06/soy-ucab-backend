package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.dto.ConfigurationDto;
import com.ucab.soy_ucab_backend.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

    @GetMapping
    public ResponseEntity<ConfigurationDto> getConfiguration(@RequestParam String email) {
        return ResponseEntity.ok(configurationService.getConfiguration(email));
    }

    @PutMapping
    public ResponseEntity<ConfigurationDto> updateConfiguration(@RequestParam String email, @RequestBody ConfigurationDto dto) {
        return ResponseEntity.ok(configurationService.updateConfiguration(email, dto));
    }
}
