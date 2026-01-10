package com.ucab.soy_ucab_backend.service;

import com.ucab.soy_ucab_backend.dto.catalog.UnregisteredCatalogDto;
import com.ucab.soy_ucab_backend.repository.CatalogoOficialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    @Autowired
    private CatalogoOficialRepository catalogoRepository;

    public UnregisteredCatalogDto getUnregisteredCatalog() {
        List<Object[]> unregisteredSchoolsRaw = catalogoRepository.findUnregisteredSchools();
        List<String> unregisteredFaculties = catalogoRepository.findUnregisteredFaculties();

        List<UnregisteredCatalogDto.SchoolDto> schoolDtos = unregisteredSchoolsRaw.stream()
                .map(obj -> UnregisteredCatalogDto.SchoolDto.builder()
                        .name((String) obj[0])
                        .faculty((String) obj[1])
                        .build())
                .collect(Collectors.toList());

        return UnregisteredCatalogDto.builder()
                .schools(schoolDtos)
                .faculties(unregisteredFaculties)
                .build();
    }
}
