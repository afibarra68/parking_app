package com.webstore.usersMs.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class DCountry {

    private Long countryId;

    private String name;

    private String description;

    private String isoCode;

    private LocalDate timezone;

    private String lang;

    private String currency;
}
