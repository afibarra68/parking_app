package com.webstore.usersMs.controllers;

import com.webstore.usersMs.dtos.DCompany;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@Validated
@Log4j2
@RequestMapping(value = "/companies")
@Tag(name = "Empresas", description = "API para gestión de empresas")
public class CompanyController {

    private final CompanyService service;

    @Operation(summary = "Crear empresa", description = "Crea una nueva empresa en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa creada exitosamente",
                    content = @Content(schema = @Schema(implementation = DCompany.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    public DCompany create(@RequestBody DCompany company) throws WbException {
        return service.create(company);
    }

    @Operation(summary = "Actualizar empresa", description = "Actualiza la información de una empresa existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = DCompany.class))),
            @ApiResponse(responseCode = "404", description = "Empresa no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PutMapping
    public DCompany update(@RequestBody DCompany company) throws WbException {
        return service.update(company);
    }

    @Operation(summary = "Listar empresas", description = "Obtiene una lista de empresas con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de empresas obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping
    public List<DCompany> getCompanies(
            @Parameter(description = "ID de la empresa") @RequestParam(required = false) Long companyId,
            @Parameter(description = "Nombre de la empresa") @RequestParam(required = false) String companyName,
            @Parameter(description = "Número de identidad") @RequestParam(required = false) String numberIdentity) throws WbException {
        return service.getBy(companyId, companyName, numberIdentity);
    }

    @Operation(summary = "Listar empresas paginadas", description = "Obtiene una lista paginada de empresas con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de empresas obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/pageable")
    public Page<DCompany> getCompaniesPageable(
            @Parameter(description = "ID de la empresa") @RequestParam(required = false) Long companyId,
            @Parameter(description = "Nombre de la empresa") @RequestParam(required = false) String companyName,
            @Parameter(description = "Número de identidad") @RequestParam(required = false) String numberIdentity,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.findByPageable(companyId, companyName, numberIdentity, pageable);
    }

    @Operation(summary = "Obtener empresa actual", description = "Obtiene la empresa asociada al usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = DCompany.class))),
            @ApiResponse(responseCode = "404", description = "Empresa no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/current")
    public DCompany getCurrentUserCompany() throws WbException {
        return service.getCurrentUserCompany();
    }

}

