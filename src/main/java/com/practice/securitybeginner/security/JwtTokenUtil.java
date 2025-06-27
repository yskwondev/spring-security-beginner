package com.practice.securitybeginner.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.enums.Role;
import com.practice.securitybeginner.properties.JwtTokenProperties;
import com.practice.securitybeginner.util.ConvertUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
  private final ObjectMapper objectMapper;
  private final ConvertUtil convertUtil;

  public String generateRefreshToken(Authentication authentication) {
    JwtAuthenticationToken authToken = (JwtAuthenticationToken) authentication;
    ApplicationUser applicationUser = ApplicationUser.builder().userId(authToken.getDetails().getUserId()).roles(authToken.getDetails().getRoles()).build();
    return generateToken(authToken.getPrincipal().toString(), convertUtil.convertObjectToMap(applicationUser), properties.getRefreshTokenExpiredTime().toMillis());
  }

  public String generateAccessToken(Authentication authentication) {
    JwtAuthenticationToken authToken = (JwtAuthenticationToken) authentication;
    ApplicationUser applicationUser = ApplicationUser.builder().userId(authToken.getDetails().getUserId()).roles(authToken.getDetails().getRoles()).build();
    return generateToken(authToken.getPrincipal().toString(), convertUtil.convertObjectToMap(applicationUser), properties.getAccessTokenExpiredTime().toMillis());
  }

  public String generateRefreshToken(ApplicationUser applicationUser) {
    return generateToken(applicationUser.getUserId(), convertUtil.convertObjectToMap(applicationUser), properties.getRefreshTokenExpiredTime().toMillis());
  }

  public String generateAccessToken(ApplicationUser applicationUser) {
    return generateToken(applicationUser.getUserId(), convertUtil.convertObjectToMap(applicationUser), properties.getAccessTokenExpiredTime().toMillis());
  }

  private String generateToken(String subject, Map<String, Object> claims, long expiredTime) {
    long currentTime = System.currentTimeMillis();
    return Jwts.builder()
      .subject(subject)
      .claims(claims)
      .issuedAt(new Date(currentTime))
      .expiration(new Date(currentTime + expiredTime))
      .signWith(getSigningKey())
      .compact();
  }

  public String issueRefreshTokenCookie(String refreshToken) {
    // set cookie for refresh token
    ResponseCookie refreshTokenCookie = ResponseCookie.from(properties.getRefreshTokenCookieKey(), refreshToken)
        .httpOnly(true)
        .secure(properties.getRefreshTokenCookieSecure())
        .sameSite("Lax")
        .maxAge(properties.getRefreshTokenExpiredTime().toSeconds())  // 쿠키 유효기간
        .path(properties.getReIssueUrl())  // refresh token 쿠키가 포함될 경로
        .build();
    
    String cookieString = refreshTokenCookie.toString();
    log.info("=== Cookie Debug Info ===");
    log.info("Cookie Name: {}", properties.getRefreshTokenCookieKey());
    log.info("Cookie Path: {}", properties.getReIssueUrl());
    log.info("Cookie MaxAge: {} seconds", properties.getRefreshTokenExpiredTime().toSeconds());
    log.info("Cookie Secure: {}", properties.getRefreshTokenCookieSecure());
    log.info("Full Cookie String: {}", cookieString);
    log.info("========================");
    
    return cookieString;
  }

  // 인증완료 후 API 호출시, 토큰을 통한 인증객체 생성
  public Authentication getAuthentication(String token) {
    final String userId = extractClaim(token, Claims::getSubject);
    final Claims claims = extractAllClaims(token);
    final Set<Role> roles = objectMapper.convertValue(claims.get("roles"), new TypeReference<>() {}); // todo << why?
    return new JwtAuthenticationToken(userId, new ApplicationUser(), extractAuthorities(roles));
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

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
