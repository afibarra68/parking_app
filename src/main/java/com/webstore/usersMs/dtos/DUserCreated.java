package com.webstore.usersMs.dtos;

import javax.validation.constraints.NotNull;

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
public class DUserCreated {

    private Long appUserId;

    @NotNull
    private String firstName;

    @NotNull
    private String secondName;

    @NotNull
    private String lastName;

}
