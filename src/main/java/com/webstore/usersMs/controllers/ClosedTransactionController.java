package com.webstore.usersMs.controllers;

import com.webstore.usersMs.entities.enums.EtransactionStatus;
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
import org.springframework.web.bind.annotation.PathVariable;

import com.webstore.usersMs.dtos.DClosedTransaction;
import com.webstore.usersMs.services.ClosedTransactionService;
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

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@RestController
@Validated
@Log4j2
@RequestMapping(value = "/closed-transactions")
@Tag(name = "Transacciones Cerradas", description = "API para gestión de transacciones cerradas (salida de vehículos)")
public class ClosedTransactionController {

    private final ClosedTransactionService service;

    @Operation(summary = "Crear transacción cerrada", description = "Crea una nueva transacción cerrada manualmente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción cerrada creada exitosamente",
                    content = @Content(schema = @Schema(implementation = DClosedTransaction.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    public DClosedTransaction create(@RequestBody DClosedTransaction transaction) throws WbException {
        return service.create(transaction);
    }

    @Operation(summary = "Actualizar transacción cerrada", description = "Actualiza la información de una transacción cerrada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = DClosedTransaction.class))),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PutMapping
    public DClosedTransaction update(@RequestBody DClosedTransaction transaction) throws WbException {
        return service.update(transaction);
    }

    @Operation(summary = "Listar transacciones cerradas", description = "Obtiene una lista paginada de transacciones cerradas con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de transacciones obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping
    public Page<DClosedTransaction> getClosedTransactions(
            @Parameter(description = "Estado de la transacción") @RequestParam(required = false) EtransactionStatus status,
            @Parameter(description = "ID de la empresa") @RequestParam(required = false) Long companyCompanyId,
            @Parameter(description = "Fecha de operación desde (formato: YYYY-MM-DD o YYYY-MM-DDTHH:mm:ss)") @RequestParam(required = false) String operationDateFrom,
            @Parameter(description = "Fecha de operación hasta (formato: YYYY-MM-DD o YYYY-MM-DDTHH:mm:ss)") @RequestParam(required = false) String operationDateTo,
            @PageableDefault(size = 10) Pageable pageable) {
        // Convertir String a LocalDateTime si se proporcionan
        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;

        if (operationDateFrom != null && !operationDateFrom.isEmpty()) {
            try {
                // Formato esperado: "YYYY-MM-DD" o "YYYY-MM-DDTHH:mm:ss"
                if (operationDateFrom.length() == 10) {
                    // Solo fecha, agregar hora 00:00:00
                    fromDate = LocalDate.parse(operationDateFrom, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
                } else {
                    fromDate = LocalDateTime.parse(operationDateFrom, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            } catch (Exception e) {
                log.warn("Error al parsear operationDateFrom: {}", operationDateFrom, e);
            }
        }

        if (operationDateTo != null && !operationDateTo.isEmpty()) {
            try {
                // Formato esperado: "YYYY-MM-DD" o "YYYY-MM-DDTHH:mm:ss"
                if (operationDateTo.length() == 10) {
                    // Solo fecha, agregar hora 00:00:00 del día siguiente
                    toDate = LocalDate.parse(operationDateTo, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay()
                            .plusDays(1);
                } else {
                    toDate = LocalDateTime.parse(operationDateTo, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            } catch (Exception e) {
                log.warn("Error al parsear operationDateTo: {}", operationDateTo, e);
            }
        }

        return service.findBy(status, companyCompanyId, fromDate, toDate, pageable);
    }

    @Operation(summary = "Cerrar transacción", description = "Cierra una transacción abierta, calcula el monto a pagar y crea la transacción cerrada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción cerrada exitosamente",
                    content = @Content(schema = @Schema(implementation = DClosedTransaction.class))),
            @ApiResponse(responseCode = "404", description = "Transacción abierta no encontrada"),
            @ApiResponse(responseCode = "412", description = "La transacción no está en estado OPEN"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping("/close/{openTransactionId}")
    public DClosedTransaction closeTransaction(
            @Parameter(description = "ID de la transacción abierta a cerrar", required = true) @PathVariable Long openTransactionId) throws WbException {
        return service.closeTransaction(openTransactionId);
    }

    @Operation(summary = "Estadísticas del día", description = "Obtiene estadísticas de transacciones cerradas del día actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/today-stats")
    public com.webstore.usersMs.dtos.DClosedTransactionStats getTodayStats() throws WbException {
        return service.getTodayStats();
    }
}
