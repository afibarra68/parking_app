package com.webstore.usersMs.dtos;

import javax.validation.constraints.NotNull;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
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
}
