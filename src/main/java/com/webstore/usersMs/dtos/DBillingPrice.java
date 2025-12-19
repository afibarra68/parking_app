package com.webstore.usersMs.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.webstore.usersMs.error.handlers.enums.EnumResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DBillingPrice {

    @EqualsAndHashCode.Include
    private Long billingPriceId;

    private EnumResource status;

    private LocalDate dateStartDisabled;

    private String coverType;

    private Boolean applyDiscount;

    private Long discountDiscountId;

    private Long companyCompanyId;

    private Long businessServiceBusinessServiceId;

    @Deprecated
    private Integer start;

    @Deprecated
    private Integer end;

    private Integer hours;

    private Long mount;

    private EnumResource tipoVehiculo;
}
