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

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "billing_price")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class BillingPrice {

    private static final String BILLING_PRICE_ID_SEQ = "billing_price_id_seq";

    @Id
    @GeneratedValue(generator = BILLING_PRICE_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = BILLING_PRICE_ID_SEQ, sequenceName = BILLING_PRICE_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    @Column(name = "billing_price_id")
    private Long billingPriceId;

    @Column(name = "status")
    private String status;

    @Column(name = "date_start_disabled")
    private LocalDate dateStartDisabled;

    @Column(name = "cover_type")
    private String coverType;

    @Column(name = "apply_discount")
    private Boolean applyDiscount;

    @ManyToOne
    @JoinColumn(name = "discount_discount_id")
    private Discount discount;

    @ManyToOne
    @JoinColumn(name = "company_company_id")
    private Company company;

    @Column(name = "\"start\"")
    private Integer start;

    @Column(name = "\"end\"")
    private Integer end;

    @Column(name = "mount")
    private Long mount;
}

