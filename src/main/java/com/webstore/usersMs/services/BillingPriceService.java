package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DBillingPrice;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BillingPriceService {

    DBillingPrice create(DBillingPrice billingPrice) throws WbException;

    DBillingPrice update(DBillingPrice billingPrice) throws WbException;

    List<DBillingPrice> getBy(String status, Long companyCompanyId, String tipoVehiculo) throws WbException;

    Page<DBillingPrice> findByPageable(String status, Long companyCompanyId, String tipoVehiculo, Pageable pageable);

    /**
     * Calcula la tarifa según el número de horas, tipo de vehículo y el companyId.
     * El companyId se obtiene automáticamente del usuario autenticado.
     * Valida que el tiempo no exceda el rango máximo configurado para el tipo de vehículo.
     * 
     * @param hours El número de horas transcurridas
     * @param tipoVehiculo El tipo de vehículo (String)
     * @return La tarifa (BillingPrice) correspondiente, null si no se encuentra
     * @throws WbException Si el tiempo excede el rango máximo o no se encuentra tarifa
     */
    DBillingPrice calculatePriceByHours(Integer hours, String tipoVehiculo) throws WbException;
}

