package com.webstore.usersMs.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;

import javax.validation.constraints.NotNull;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_user")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class User {

    private static final String USER_ID_SEQ = "user_user_id_seq";

    @Id
    @GeneratedValue(generator = USER_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = USER_ID_SEQ, sequenceName = USER_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long appUserId;

    @NotNull
    @Column(name = "fist_name")
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String secondName;

    @NotNull
    private String secondLastname;

    private Long companyName;

    private Long companyCompanyId;

    private String numberIdentity;

    private String processorId;

    private String sha;

    @NotNull
    private String password;

    private String salt;

    private String accessCredential;

    @Column(name = "access_limit")
    private LocalDate accessLevel;
}
