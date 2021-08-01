package com.example.catalogservice.controller;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.service.CatalogService;
import com.example.catalogservice.vo.ResponseCatalog;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("catalog-service")
@RestController
public class CatalogController {
    private final Environment env;
    private final CatalogService catalogService;

    @GetMapping("/health_check")
    public String status() {
        return String.format("[CatalogController.status] It's working!\n" +
                "port: %s", env.getProperty("local.server.port"));
    }

    @GetMapping("/catalogs")
    public ResponseEntity<List<ResponseCatalog>> getUsers() {
        List<CatalogEntity> allCatalogs = (List<CatalogEntity>) catalogService.getAllCatalogs();
        return ResponseEntity.status(HttpStatus.OK)
                .body(allCatalogs.stream()
                        .map(catalogEntity -> new ModelMapper().map(catalogEntity, ResponseCatalog.class))
                    .collect(Collectors.toList())
                );
    }
}
