package com.webstore.usersMs.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "closed_transaction")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ClosedTransaction {

    private static final String CLOSED_TRANSACTION_ID_SEQ = "closed_transaction_id_seq";

    @Id
    @GeneratedValue(generator = CLOSED_TRANSACTION_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = CLOSED_TRANSACTION_ID_SEQ, sequenceName = CLOSED_TRANSACTION_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    @Column(name = "closed_transaction_id")
    private Long closedTransactionId;

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
    private String status;

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
    @JoinColumn(name = "seller_app_user_id")
    private User sellerAppUser;

    @Column(name = "seller_name")
    private String sellerName;

    @Column(name = "contractor")
    private Long contractor;
}

