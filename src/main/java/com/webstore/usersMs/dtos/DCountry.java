package com.webstore.usersMs.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DCountry {

    private Long countryId;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private String isoCode;

    private String timezone;

    @NotNull
    private String lang;

    @NotNull
    private String currency;
}
