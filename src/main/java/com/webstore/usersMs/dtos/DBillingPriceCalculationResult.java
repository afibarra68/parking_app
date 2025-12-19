package com.webstore.usersMs.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.webstore.usersMs.entities.enums.ETipoVehiculo;
import com.webstore.usersMs.error.handlers.enums.EnumResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.webstore.usersMs.entities.BillingPrice;

/**
 * DTO que contiene el resultado del cálculo de tarifa basado en horas transcurridas
 * y tipo de vehículo. Abstrae toda la información necesaria para actualizar
 * una transacción abierta al momento de cerrarla.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DBillingPriceCalculationResult {

    /**
     * Número de horas calculadas para facturación (redondeo hacia arriba)
     */
    private Integer hoursForBilling;

    /**
     * Precio por hora obtenido del tipo de vehículo
     */
    private Double pricePerHour;

    /**
     * Monto total calculado: pricePerHour * hoursForBilling
     */
    private Long totalAmount;

    /**
     * Tiempo transcurrido formateado como string (ej: "3 horas 15 minutos")
     */
    private String timeElapsed;

    /**
     * Tarifa de referencia (BillingPrice de 1 hora) para asociar a la transacción
     */
    private BillingPrice billingPrice;

    /**
     * ID del servicio de negocio obtenido del billingPrice
     */
    private Long serviceTypeServiceTypeId;

    /**
     * Tipo de vehículo utilizado en el cálculo
     */

    private EnumResource tipoVehiculo;
}

