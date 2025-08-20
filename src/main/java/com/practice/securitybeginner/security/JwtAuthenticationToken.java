package com.practice.securitybeginner.security;

import com.practice.securitybeginner.domain.ApplicationUser;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

// JWT인증 security 토큰 객체
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

  private final String principal; // 사용자 ID
  private String credentials; // 비밀번호
  private final ApplicationUser details;

  private JwtAuthenticationToken(String principal, String credentials, ApplicationUser details, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    this.details = details;
    setAuthenticated(authorities != null && !authorities.isEmpty());
  }

  // 인증 전 임시토큰 생성
  public static JwtAuthenticationToken unauthenticated(String userId, String password) {
    return new JwtAuthenticationToken(userId, password, null, null);
  }

  // 인증 완료 토큰 생성
  public static JwtAuthenticationToken authenticated(ApplicationUser user, Collection<? extends GrantedAuthority> authorities) {
    return new JwtAuthenticationToken(user.getUserId(), null, user, authorities);
  }

  @Override
  public Object getCredentials() {
    return credentials;
  }

  @Override
  public Object getPrincipal() {
    return principal;
  }

  @Override
  public ApplicationUser getDetails() {
    return details;
  }
}
