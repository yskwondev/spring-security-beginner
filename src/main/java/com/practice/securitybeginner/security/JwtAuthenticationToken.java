package com.practice.securitybeginner.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

// JWT인증 security 토큰 객체
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

  private final String principal;
  private final String credentials;
  private final Map<String, Object> details;

  // 인증 전 임시토큰 생성
  public JwtAuthenticationToken(String principal, String credentials) {
    super(null);
    this.principal = principal;
    this.credentials = credentials; // 비밀번호 (인증 전)
    this.details = null;
    setAuthenticated(false);
  }

  // 전체 인증완료 토큰 생성
  public JwtAuthenticationToken(String pricipal, Map<String, Object> details, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = pricipal;
    this.credentials = null;
    this.details = details;
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

  @Override
  public Map<String, Object> getDetails() {
    return details;
  }
}
