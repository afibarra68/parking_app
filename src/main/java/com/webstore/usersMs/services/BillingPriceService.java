package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DBillingPrice;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BillingPriceService {

    DBillingPrice create(DBillingPrice billingPrice) throws WbException;

    DBillingPrice update(DBillingPrice billingPrice) throws WbException;

    List<DBillingPrice> getBy(String status, Long companyCompanyId, String coverType) throws WbException;

    Page<DBillingPrice> findByPageable(String status, Long companyCompanyId, String coverType, Pageable pageable);

    /**
     * Calcula la tarifa según el número de horas y el companyId.
     * El companyId se obtiene automáticamente del usuario autenticado.
     * 
     * @param hours El número de horas transcurridas
     * @return La tarifa (BillingPrice) correspondiente, null si no se encuentra
     */
    DBillingPrice calculatePriceByHours(Integer hours) throws WbException;
}

