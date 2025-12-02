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
public class DUserList {

    @EqualsAndHashCode.Include
    private Long appUserId;

    private String firstName;

    private String secondName;

    private String lastName;

    private String secondLastname;

    private String numberIdentity;

    private String processorId;

    private String sha;

    private String salt;

    private String accessCredential;

    private LocalDate accessLevel;

    private Long companyCompanyId;

    private String companyName;
}

