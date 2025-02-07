package com.practice.securitybeginner.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.enums.Role;
import com.practice.securitybeginner.properties.JwtTokenProperties;
import com.practice.securitybeginner.security.domain.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtTokenProperties.class)
public class JwtTokenUtil {

  private final JwtTokenProperties properties;
  private final UserDetailsService userDetailsService;
  private final ObjectMapper objectMapper;

  public String generateToken(Authentication authentication) {
    JwtAuthenticationToken authToken = (JwtAuthenticationToken) authentication;
    long currentTime = System.currentTimeMillis();
    return Jwts.builder()
      .subject(authToken.getPrincipal().toString())
      .claims(authToken.getDetails())
      .issuedAt(new Date(currentTime))
      .expiration(new Date(currentTime + properties.getExpiredTime().toMillis()))
      .signWith(getSigningKey())
      .compact();
  }

  // 인증완료 후 API 호출시, 토큰을 통한 인증객체 생성
  public Authentication getAuthentication(String token) {
    final String userId = extractClaim(token, Claims::getSubject);
    final Claims claims = extractAllClaims(token);
    final Set<Role> roles = objectMapper.convertValue(claims.get("roles"), new TypeReference<>() {});
    return new JwtAuthenticationToken(userId, claims, extractAuthorities(roles));
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  // todo 사용자명이랑 토큰에 들어있는 사용자명이랑 비교 안해도 되나?
  public boolean validateToken(String token) {
//    final String userId = extractClaim(token, Claims::getSubject);
    return !isTokenExpired(token);
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  public boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Base64.getEncoder().encodeToString(properties.getSecretKey().getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private Collection<? extends GrantedAuthority> extractAuthorities(Set<Role> roles) {
    return roles
      .stream()
      .map(Role::getRoleAuthorities)
      .flatMap(Collection::stream)
      .toList();
//      .collect(Collectors.toSet());
  }

}
