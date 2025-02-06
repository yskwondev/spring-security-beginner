package com.practice.securitybeginner.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

// JWT 인증 토큰 객체
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

  private final String principal;
  private final Object credentials;

  public JwtAuthenticationToken(String token) {
    super(null);
    this.credentials = token;
    this.principal = null;
    setAuthenticated(false);
  }

  // 인증 전 임시토큰 생성
  public JwtAuthenticationToken(String principal, String credentials) {
    super(null);
    this.credentials = credentials;
    this.principal = principal;
    setAuthenticated(false);
  }

  // 전체 인증완료 토큰 생성
  public JwtAuthenticationToken(String pricipal, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.credentials = null;
    this.principal = pricipal;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return credentials;
  }

  @Override
  public Object getPrincipal() {
    return principal;
  }

}
