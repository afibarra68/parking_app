package com.webstore.usersMs.controllers;

import com.webstore.usersMs.dtos.DBillingPrice;
import com.webstore.usersMs.entities.enums.EBillingStatus;
import com.webstore.usersMs.entities.enums.ETipoVehiculo;
import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.error.handlers.enums.EnumResource;
import com.webstore.usersMs.services.BillingPriceService;
import com.webstore.usersMs.services.EnumResourceService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@Validated
@Log4j2
@RequestMapping(value = "/billing-prices")
@Tag(name = "Tarifas", description = "API para gestión de tarifas de parqueadero")
public class BillingPriceController {

    private final BillingPriceService service;
    private final EnumResourceService enumResourceService;

    @Operation(summary = "Crear tarifa", description = "Crea una nueva tarifa de parqueadero basada en horas transcurridas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarifa creada exitosamente",
                    content = @Content(schema = @Schema(implementation = DBillingPrice.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    public DBillingPrice create(@RequestBody DBillingPrice billingPrice) throws WbException {
        return service.create(billingPrice);
    }

    @Operation(summary = "Actualizar tarifa", description = "Actualiza la información de una tarifa existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarifa actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = DBillingPrice.class))),
            @ApiResponse(responseCode = "404", description = "Tarifa no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PutMapping
    public DBillingPrice update(@RequestBody DBillingPrice billingPrice) throws WbException {
        return service.update(billingPrice);
    }

    @Operation(summary = "Eliminar tarifa", description = "Elimina una tarifa del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarifa eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tarifa no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @DeleteMapping("/{billingPriceId}")
    public void delete(
            @Parameter(description = "ID de la tarifa a eliminar", required = true) @PathVariable Long billingPriceId) throws WbException {
        service.delete(billingPriceId);
    }

    @Operation(summary = "Listar tarifas", description = "Obtiene una lista de tarifas con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tarifas obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping
    public List<DBillingPrice> getBillingPrices(
            @Parameter(description = "Estado de la tarifa") @RequestParam(required = false) EBillingStatus status,
            @Parameter(description = "ID de la empresa") @RequestParam(required = false) Long companyCompanyId,
            @Parameter(description = "Tipo de vehículo") @RequestParam(required = false) ETipoVehiculo tipoVehiculo) throws WbException {
        return service.getBy(status, companyCompanyId, tipoVehiculo);
    }

    @Operation(summary = "Listar tarifas paginadas", description = "Obtiene una lista paginada de tarifas con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de tarifas obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/pageable")
    public Page<DBillingPrice> getBillingPricesPageable(
            @Parameter(description = "Estado de la tarifa") @RequestParam(required = false) EBillingStatus status,
            @Parameter(description = "ID de la empresa") @RequestParam(required = false) Long companyCompanyId,
            @Parameter(description = "Tipo de vehículo") @RequestParam(required = false) ETipoVehiculo tipoVehiculo,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.findByPageable(status, companyCompanyId, tipoVehiculo, pageable);
    }

    @Operation(summary = "Obtener enums para formularios", description = "Obtiene los enums necesarios para crear y actualizar tarifas (EBillingStatus y ETipoVehiculo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enums obtenidos exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/enums")
    public Map<String, List<EnumResource>> getEnumsForForms() {
        Map<String, List<EnumResource>> enums = new HashMap<>();
        enums.put("status", enumResourceService.getEnumResources(EBillingStatus.class));
        enums.put("tipoVehiculo", enumResourceService.getEnumResources(ETipoVehiculo.class));
        return enums;
    }
}

