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

    private LocalDate startDay;

    private LocalTime startTime;

    private LocalTime endDate;

    private LocalDate endTime;

    private String currency;

    private Long companyId;

    private Long companyDescription;

    private String nit;

    private String identity;

    private EnumResource status;

    private Long BillingPriceId;

    private Double amount;

    private String discount;

    private Double totalAmount;

    private String timeElapsed;

    private LocalDateTime operationDate;

    private Long serviceId;

    private Long sellerName;

    private String vehiclePlate;

    private String vehiculeType;

    private String printerType;

}
