package com.ucab.soy_ucab_backend.controller;

import com.ucab.soy_ucab_backend.dto.catalog.UnregisteredCatalogDto;
import com.ucab.soy_ucab_backend.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @GetMapping("/unregistered")
    public ResponseEntity<UnregisteredCatalogDto> getUnregisteredCatalog() {
        return ResponseEntity.ok(catalogService.getUnregisteredCatalog());
    }
}
