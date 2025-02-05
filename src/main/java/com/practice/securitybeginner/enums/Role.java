package com.practice.securitybeginner.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.practice.securitybeginner.enums.Permission.*;

@Getter
@RequiredArgsConstructor
public enum Role {
  ADMIN(Set.of(
          READ,
          CREATE,
          UPDATE,
          DELETE
  )),
  MANAGER(Set.of(
          READ,
          CREATE,
          UPDATE
  )),
  USER(Set.of(
          READ
  ));

  private final Set<Permission> permissions;

  public List<SimpleGrantedAuthority> getRoleAuthorities() {
      List<SimpleGrantedAuthority> authorities = getPermissions()
              .stream()
              .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
              .collect(Collectors.toList());

      authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

      return authorities;
  }
}
