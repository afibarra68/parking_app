package com.webstore.usersMs.controllers;

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

import com.webstore.usersMs.dtos.DOpenTransaction;
import com.webstore.usersMs.services.OpenTransactionService;
import com.webstore.usersMs.error.WbException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@RestController
@Validated
@Log4j2
@RequestMapping(value = "/open-transactions")
@Tag(name = "Transacciones Abiertas", description = "API para gestión de transacciones abiertas (ingreso de vehículos)")
public class OpenTransactionController {

    private final OpenTransactionService service;

    @Operation(summary = "Crear transacción abierta", description = "Registra el ingreso de un vehículo al parqueadero")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción abierta creada exitosamente",
                    content = @Content(schema = @Schema(implementation = DOpenTransaction.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    public DOpenTransaction create(@RequestBody DOpenTransaction transaction) throws WbException {
        return service.create(transaction);
    }

    @Operation(summary = "Actualizar transacción abierta", description = "Actualiza la información de una transacción abierta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = DOpenTransaction.class))),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PutMapping
    public DOpenTransaction update(@RequestBody DOpenTransaction transaction) throws WbException {
        return service.update(transaction);
    }

    @Operation(summary = "Listar transacciones abiertas", description = "Obtiene una lista paginada de transacciones abiertas con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de transacciones obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping
    public Page<DOpenTransaction> getOpenTransactions(
            @Parameter(description = "Estado de la transacción") @RequestParam(required = false) String status,
            @Parameter(description = "ID de la empresa") @RequestParam(required = false) Long companyCompanyId,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.findBy(status, companyCompanyId, pageable);
    }

    @Operation(summary = "Buscar transacción por placa", description = "Busca una transacción abierta por la placa del vehículo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción encontrada exitosamente",
                    content = @Content(schema = @Schema(implementation = DOpenTransaction.class))),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/by-plate")
    public DOpenTransaction getByVehiclePlate(
            @Parameter(description = "Placa del vehículo", required = true) @RequestParam String vehiclePlate) throws WbException {
        return service.findByVehiclePlate(vehiclePlate);
    }
}

