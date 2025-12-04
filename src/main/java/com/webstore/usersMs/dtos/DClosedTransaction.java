package com.webstore.usersMs.dtos;

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
public class DClosedTransaction {

    private Long closedTransactionId;

    private LocalTime startTime;

    private LocalDate startDay;

    private LocalTime endDate;

    private LocalDate endTime;

    private String currency;

    private Long companyCompanyId;

    private String status;

    private Long billingPriceBillingPriceId;

    private Double amount;

    private String discount;

    private Double totalAmount;

    private String timeElapsed;

    private LocalDateTime operationDate;

    private Long serviceTypeServiceTypeId;

    private Long sellerAppUserId;

    private String sellerName;

    private Long contractor;

    private String countryCurrency;
}

