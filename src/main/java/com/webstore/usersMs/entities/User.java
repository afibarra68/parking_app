package com.webstore.usersMs.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;

import javax.validation.constraints.NotNull;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class User {

    private static final String USER_ID_SEQ = "user_user_id_seq";
    @Id
    @GeneratedValue(generator = USER_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = USER_ID_SEQ, sequenceName = USER_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long userId;
    @NotNull
    private String firstName;

    @NotNull
    private String hashedPassword;

    @NotNull
    private String secondName;

    @NotNull
    private String lastName;

    @NotNull
    private String secondLastName;

    @NotNull
    private String phoneNumber;

    private String salt;
}
