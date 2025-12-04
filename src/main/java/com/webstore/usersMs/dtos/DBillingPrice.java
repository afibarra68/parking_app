package com.webstore.usersMs.dtos;

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
public class DBillingPrice {

    @EqualsAndHashCode.Include
    private Long billingPriceId;

    private String status;

    private LocalDate dateStartDisabled;

    private String coverType;

    private Boolean applyDiscount;

    private Long discountDiscountId;

    private Long companyCompanyId;

    private Long businessServiceBusinessServiceId;

    private Integer start;

    private Integer end;

    private Long mount;

    private String tipoVehiculo;
}

