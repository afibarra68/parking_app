package com.webstore.usersMs.dtos;

import com.webstore.usersMs.entities.Product;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class DCompany {

    private Long companyId;

    private String companyName;

    private String numberIdentity;

    private DCountry country;

}
