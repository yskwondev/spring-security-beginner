package com.practice.securitybeginner.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.enums.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

  private ApplicationUser user;

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getUserId();
  }

  @Override
  public boolean isAccountNonExpired() {
    return !user.isAccountEnabled();
  }

  @Override
  public boolean isAccountNonLocked() {
    return !user.isAccountLocked();
  }

  @Override
  public boolean isEnabled() {
    return user.isAccountEnabled();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return user.getRoles()
      .stream()
      .map(Role::getRoleAuthorities)
      .flatMap(Collection::stream)
      .toList();
//      .collect(Collectors.toSet());
  }

}
