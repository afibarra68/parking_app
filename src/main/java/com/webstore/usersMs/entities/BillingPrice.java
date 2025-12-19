package com.webstore.usersMs.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;

import com.webstore.usersMs.entities.enums.ETipoVehiculo;
import com.webstore.usersMs.entities.enums.EBillingStatus;
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
    @Enumerated(EnumType.STRING)
    private EBillingStatus status;

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

    @ManyToOne
    @JoinColumn(name = "business_service_business_service_id")
    private BusinessService businessService;

    @Column(name = "\"start\"")
    @Deprecated
    private Integer start;

    @Column(name = "\"end\"")
    @Deprecated
    private Integer end;

    @Column(name = "hours")
    private Integer hours;

    @Column(name = "mount")
    private Long mount;

    @Column(name = "vehicule_type")
    @Enumerated(EnumType.STRING)
    private ETipoVehiculo tipoVehiculo;
}

