package com.webstore.usersMs.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotNull;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class DUser {

    private Long appUserId;

    private String firstName;

    private String secondName;

    private String lastName;

    private String secondLastName;

    @NotNull
    private String numberIdentity;

    private String sha;

    private String password;

    private String phoneNumber;

    private String salt;

    private String accessCredential;

    private LocalDate accessLevel;

    private Long companyCompanyId;

    private String processorId;
}
