package com.webstore.usersMs.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class DClient {

    private Long clientId;

    private String fullName;

    private String typeIdentity;

    private String numberIdentity;

    private String people;

    private String paymentDay;

    private Long clientCompanyId;

}
