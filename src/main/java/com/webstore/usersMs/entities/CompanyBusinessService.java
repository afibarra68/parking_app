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

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company_business_service")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class CompanyBusinessService {

    private static final String COMPANY_BUSINESS_SERVICE_ID_SEQ = "company_business_service_company_business_service_id_seq";

    @Id
    @GeneratedValue(generator = COMPANY_BUSINESS_SERVICE_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = COMPANY_BUSINESS_SERVICE_ID_SEQ, sequenceName = COMPANY_BUSINESS_SERVICE_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long companyBusinessServiceId;

    @ManyToOne
    @JoinColumn(name = "company_company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "business_service_business_service_id")
    private BusinessService businessService;

    private LocalDateTime createdDate;

}

