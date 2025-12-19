package com.webstore.usersMs.controllers;

import com.webstore.usersMs.dtos.DCountry;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.services.CountryService;
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
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping(value = "/countries")
@Tag(name = "Países", description = "API para gestión de países")
public class CountryController {

    private final CountryService service;

    @Operation(summary = "Crear país", description = "Crea un nuevo país en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "País creado exitosamente", content = @Content(schema = @Schema(implementation = DCountry.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    public DCountry create(@RequestBody DCountry client) throws WbException {
        return service.create(client);
    }

    @Operation(summary = "Actualizar país", description = "Actualiza la información de un país existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "País actualizado exitosamente", content = @Content(schema = @Schema(implementation = DCountry.class))),
            @ApiResponse(responseCode = "404", description = "País no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PutMapping
    public DCountry update(@RequestBody DCountry client) throws WbException {
        return service.update(client);
    }

    @Operation(summary = "Listar países", description = "Obtiene una lista de países con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de países obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping
    public List<DCountry> getClients(
            @Parameter(description = "ID del país") @RequestParam(required = false) Long countryId,
            @Parameter(description = "Descripción del país") @RequestParam(required = false) String description)
            throws WbException {
        return service.getBy(countryId, description);
    }

    @Operation(summary = "Listar países paginados", description = "Obtiene una lista paginada de países con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de países obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/pageable")
    public Page<DCountry> getCountriesPageable(
            @Parameter(description = "ID del país") @RequestParam(required = false) Long countryId,
            @Parameter(description = "Descripción del país") @RequestParam(required = false) String description,
            @Parameter(description = "Nombre del país") @RequestParam(required = false) String name,
            @PageableDefault(size = 10) Pageable pageable) throws WbException {
        return service.findByPageable(countryId, description, name, pageable);
    }

    @Operation(summary = "Listar países consultables", description = "Obtiene una lista de países para consultas con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de países obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/queryable")
    public List<DCountry> getCountriesQueryable(
            @Parameter(description = "ID del país") @RequestParam(required = false) Long countryId,
            @Parameter(description = "Descripción del país") @RequestParam(required = false) String description,
            @Parameter(description = "Nombre del país") @RequestParam(required = false) String name)
            throws WbException {
        return service.findByQueryable(countryId, description, name);
    }

    @Operation(summary = "Eliminar país", description = "Elimina un país del sistema. No se puede eliminar si tiene empresas asociadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "País eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "País no encontrado"),
            @ApiResponse(responseCode = "412", description = "El país no se puede eliminar porque tiene relaciones con otras entidades (empresas)"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @DeleteMapping("/{id}")
    public void deleteById(
            @Parameter(description = "ID del país a eliminar", required = true) @PathVariable("id") Long countryId)
            throws WbException {
        service.delete(countryId);
    }

}
