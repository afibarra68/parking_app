package com.webstore.usersMs.entities;

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

import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "country")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Country {

    private static final String USER_ID_SEQ = "country_country_id_seq";

    @Id
    @GeneratedValue(generator = USER_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = USER_ID_SEQ, sequenceName = USER_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long countryId;

    private String name;

    private String description;

    private String isoCode;

    private String timezone;

    private String lang;

    private String currency;

}