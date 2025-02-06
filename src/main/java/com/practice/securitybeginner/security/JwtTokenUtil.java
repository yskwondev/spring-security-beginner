package com.practice.securitybeginner.security;

import com.practice.securitybeginner.properties.JwtTokenProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtTokenProperties.class)
public class JwtTokenUtil {

  private final JwtTokenProperties properties;
  private final UserDetailsService userDetailsService;
  private final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

  public String generateToken(Authentication authentication) {
    long currentTime = System.currentTimeMillis();
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return Jwts.builder()
      .subject(userDetails.getUsername())
      .issuedAt(new Date(currentTime))
      .expiration(new Date(currentTime + properties.getExpiredTime().toMillis()))
      .signWith(getSigningKey())
      .compact();
  }

  // 인증완료
  public Authentication getAuthentication(String token) {
    final String userId = extractClaim(token, Claims::getSubject);
    UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
    return new JwtAuthenticationToken(userDetails.getUsername(), userDetails.getAuthorities());
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Base64.getEncoder().encodeToString(properties.getSecretKey().getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
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

}
