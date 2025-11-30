package com.webstore.usersMs.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;

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

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "business_service")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class BusinessService {

    private static final String BUSINESS_SERVICE_ID_SEQ = "business_service_business_service_id_seq";

    @Id
    @GeneratedValue(generator = BUSINESS_SERVICE_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = BUSINESS_SERVICE_ID_SEQ, sequenceName = BUSINESS_SERVICE_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long businessServiceId;

    private String principalName;

    private String description;

    private String code;

    private LocalDateTime createdDate;

}

