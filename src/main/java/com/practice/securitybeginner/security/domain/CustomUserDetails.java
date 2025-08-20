package com.practice.securitybeginner.security.domain;

import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

  private ApplicationUser user;

  public int getId() {
    return user.getId();
  }

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
