package com.webstore.usersMs.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
@Table(name = "discount")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Discount {

    private static final String DISCOUNT_ID_SEQ = "discount_id_seq";

    @Id
    @GeneratedValue(generator = DISCOUNT_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = DISCOUNT_ID_SEQ, sequenceName = DISCOUNT_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    @Column(name = "discount_id")
    private Long discountId;

    @Column(name = "percent_discount")
    private Double percentDiscount;

    @Column(name = "name_discount")
    private String nameDiscount;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "applicable_discount")
    private Boolean applicableDiscount;

    @Column(name = "status")
    private String status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}

