package com.practice.securitybeginner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.practice.securitybeginner.enums.Role;
import com.practice.securitybeginner.security.domain.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUser {

  private Integer id;
  private String userId;
  private String userName;
  @JsonIgnore
  private String password;
  @JsonIgnore
  private boolean accountLocked;
  @JsonIgnore
  private boolean accountEnabled;
  private LocalDateTime lastLoginDateTime;
  private LocalDate createDate;
  private Set<Role> roles;

  public static ApplicationUser from(JwtToken token) {
    return ApplicationUser.builder()
      .userId(token.getUserId())
      .roles(token.getClaims().getRoles())
      .build();
  }

  // 최종 로그인일시 업데이트
  public void updateLastLoginDateTime() {
    this.lastLoginDateTime = LocalDateTime.now();
  }

  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles
      .stream()
      .map(Role::getRoleAuthorities)
      .flatMap(Collection::stream)
      .toList();
  }

}
