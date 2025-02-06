package com.practice.securitybeginner.domain;

import com.practice.securitybeginner.enums.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUser implements UserDetails, Principal, Serializable {

  @Serial
  private static final long serialVersionUID = 7981231384125L;

  private String email;
  private String password;
  private String userName;
  private boolean accountLocked;
  private boolean accountEnabled;
  private LocalDateTime lastLoginDate;
  private LocalDate createDate;
  private Set<Role> roles;

  @Override
  public String getName() {
    return email;
  }

  @Override
  public boolean isEnabled() {
    return accountEnabled;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !accountLocked;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles
      .stream()
      .map(Role::getRoleAuthorities)
      .flatMap(Collection::stream)
      .toList();
//      .collect(Collectors.toSet());
  }

}
