package com.webstore.usersMs.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;

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

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Company {

    private static final String COMPANY_ID_SEQ = "company_company_id_seq";

    @Id
    @GeneratedValue(generator = COMPANY_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = COMPANY_ID_SEQ, sequenceName = COMPANY_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long companyId;

    private String companyName;

    private String numberIdentity;

    @ManyToOne
    @JoinColumn(name = "country_country_id")
    private Country country;

}

