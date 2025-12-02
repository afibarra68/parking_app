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
public class DUserRoleList {

    @EqualsAndHashCode.Include
    private Long userRoleId;

    private Long appUserId;

    private String firstName;

    private String lastName;

    private String secondName;

    private String secondLastname;

    private String numberIdentity;

    private String role;

    private Long companyCompanyId;

    private String companyName;
}

