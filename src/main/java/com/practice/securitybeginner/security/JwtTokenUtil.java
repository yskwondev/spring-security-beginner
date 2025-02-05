package com.practice.securitybeginner.security;

import com.practice.securitybeginner.properties.JwtTokenProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

  private SecretKey getSigningKey() {
    byte[] keyBytes = Base64.getEncoder().encodeToString(properties.getSecretKey().getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String userId = extractClaim(token, Claims::getSubject);
    return userId.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  public boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }

  public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
    long currentTime = System.currentTimeMillis();
    return Jwts.builder()
      .subject(userDetails.getUsername())
      .issuedAt(new Date(currentTime))
      .expiration(new Date(currentTime + properties.getExpiredTime().toMillis()))
      .claim("authorities", userDetails.getAuthorities())
      .claims(extraClaims)
      .signWith(getSigningKey())
      .compact();
  }

}
