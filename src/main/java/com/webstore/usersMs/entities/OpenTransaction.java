package com.webstore.usersMs.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;

import com.webstore.usersMs.entities.enums.EtransactionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.webstore.usersMs.dtos.DCompany;
import com.webstore.usersMs.entities.enums.ETipoVehiculo;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_transaction")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class OpenTransaction {

    private static final String OPEN_TRANSACTION_ID_SEQ = "open_transaction_id_seq";

    @Id
    @GeneratedValue(generator = OPEN_TRANSACTION_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = OPEN_TRANSACTION_ID_SEQ, sequenceName = OPEN_TRANSACTION_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    @Column(name = "open_transaction_id")
    private Long openTransactionId;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "start_day")
    private LocalDate startDay;

    @Column(name = "end_date")
    private LocalTime endDate;

    @Column(name = "end_tyme")
    private LocalDate endTime;

    @Column(name = "currency")
    private String currency;

    @ManyToOne
    @JoinColumn(name = "company_company_id")
    private Company company;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EtransactionStatus status;

    @ManyToOne
    @JoinColumn(name = "billing_price_billing_price_id")
    private BillingPrice billingPrice;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "discount")
    private String discount;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "time_elapsed")
    private String timeElapsed;

    @Column(name = "operation_date")
    private LocalDateTime operationDate;

    @Column(name = "service_type_service_type_id")
    private Long serviceTypeServiceTypeId;

    @ManyToOne
    @JoinColumn(name = "app_user_app_user_seller")
    private User appUserSeller;

    @Column(name = "vehicle_plate")
    private String vehiclePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo")
    private ETipoVehiculo tipoVehiculo;
}

