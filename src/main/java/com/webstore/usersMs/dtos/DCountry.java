package com.webstore.usersMs.dtos;

import com.webstore.usersMs.entities.Product;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

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
