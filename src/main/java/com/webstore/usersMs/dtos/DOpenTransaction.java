package com.webstore.usersMs.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.webstore.usersMs.error.handlers.enums.EnumResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DOpenTransaction {

    private Long openTransactionId;

    private LocalTime startTime;

    private LocalDate startDay;

    private LocalTime endDate;

    private LocalDate endTime;

    private String currency;

    private Long companyCompanyId;

    private EnumResource status;

    private Long billingPriceBillingPriceId;

    private Double amount;

    private String discount;

    private Double totalAmount;

    private String timeElapsed;

    private LocalDateTime operationDate;

    private Long serviceTypeServiceTypeId;

    private Long appUserAppUserSeller;

    private String vehiclePlate;

    private String tipoVehiculo;
}

