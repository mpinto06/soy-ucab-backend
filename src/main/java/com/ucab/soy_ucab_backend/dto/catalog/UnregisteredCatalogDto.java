package com.ucab.soy_ucab_backend.dto.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnregisteredCatalogDto {
    private List<SchoolDto> schools;
    private List<String> faculties;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SchoolDto {
        private String name;
        private String faculty;
    }
}
