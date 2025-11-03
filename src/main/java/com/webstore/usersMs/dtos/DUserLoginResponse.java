package com.webstore.usersMs.dtos;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.stream.Collectors.toList;

import javax.validation.constraints.NotNull;

import jakarta.persistence.Column;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DUserLoginResponse implements Serializable {

   @NotNull
   private String jwt;

   private String firstName;

   @NotNull
   private String lastName;

   @NotNull
   private String secondName;

   @NotNull
   private String secondLastname;

   @NotNull
   private String tokenType;

   @NotNull
   private Long appUserId;

   private String numberIdentity;

   @NotNull
   private List<String> roles;

   private Long companyName;

   private Long companyDescription;

   private Boolean pwdMsgToExpire;

   private LocalDate accessLevel;


}