package com.practice.securitybeginner.security.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.enums.Role;
import com.practice.securitybeginner.enums.TokenType;
import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class JwtTokenClaims {

  private String type;
  private Set<Role> roles;

  public static JwtTokenClaims from(TokenType tokenType, ApplicationUser applicationUser) {
    return JwtTokenClaims.builder()
      .type(tokenType.name())
      .roles(applicationUser.getRoles())
      .build();
  }

  public static JwtTokenClaims from(Claims claims, ObjectMapper objectMapper) {
    return JwtTokenClaims.builder()
      .type(claims.get("type", String.class))
      .roles(objectMapper.convertValue(claims.get("roles"), new TypeReference<Set<Role>>() {}))
      .build();
  }

}
