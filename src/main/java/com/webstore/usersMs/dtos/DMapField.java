package com.webstore.usersMs.dtos;

import com.webstore.usersMs.error.handlers.enums.EnumResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DMapField {

    private String startDay;

    private String startTime;

    private String endDate;

    private String endTime;

    private String currency;

    private String companyId;

    private String companyDescription;

    private String nit;

    private String identity;

    private String status;

    private String BillingPriceId;

    private String amount;

    private String discount;

    private String totalAmount;

    private String timeElapsed;

    private String operationDate;

    private String serviceId;

    private String sellerName;

    private String vehiclePlate;

    private String vehiculeType;

    private String printerType;

}
