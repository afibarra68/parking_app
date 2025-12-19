package com.webstore.usersMs.services;

import com.webstore.usersMs.dtos.DBillingPrice;
import com.webstore.usersMs.dtos.DBillingPriceCalculationResult;
import com.webstore.usersMs.entities.enums.EBillingStatus;
import com.webstore.usersMs.entities.enums.ETipoVehiculo;
import com.webstore.usersMs.error.WbException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BillingPriceService {

    DBillingPrice create(DBillingPrice billingPrice) throws WbException;

    DBillingPrice update(DBillingPrice billingPrice) throws WbException;

    void delete(Long billingPriceId) throws WbException;

    List<DBillingPrice> getBy(EBillingStatus status, Long companyCompanyId, ETipoVehiculo tipoVehiculo) throws WbException;

    Page<DBillingPrice> findByPageable(EBillingStatus status, Long companyCompanyId, ETipoVehiculo tipoVehiculo,
                                       Pageable pageable);

    DBillingPrice calculatePriceByHours(Integer hours, String tipoVehiculo) throws WbException;

    Double getPricePerHourByTipoVehiculo(ETipoVehiculo tipoVehiculo) throws WbException;

    DBillingPriceCalculationResult calculateBillingPriceForTransaction(
            java.time.LocalDate startDay,
            java.time.LocalTime startTime,
            ETipoVehiculo tipoVehiculo) throws WbException;
}
