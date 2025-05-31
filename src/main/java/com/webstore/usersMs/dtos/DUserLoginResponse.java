package com.webstore.usersMs.dtos;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.stream.Collectors.toList;

import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class DUserLoginResponse implements UserDetails {

   @NotNull
   private String jwt;

   private String fistName;
   @NotNull
   private String secondName;

   @NotNull
   private String lastName;

   @NotNull
   private String phoneNumber;

   @NotNull
   private String tokenType;

   @NotNull
   private Long userId;

   @NotNull
   private List<String> roles;


   private Boolean pwdMsgToExpire;


   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
      return roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
   }

   @Override
   public String getPassword() {
      return null;
   }

   @Override
   public String getUsername() {
      return null;
   }

   @Override
   public boolean isAccountNonExpired() {
      return false;
   }

   @Override
   public boolean isAccountNonLocked() {
      return false;
   }

   @Override
   public boolean isCredentialsNonExpired() {
      return false;
   }

   @Override
   public boolean isEnabled() {
      return false;
   }
}