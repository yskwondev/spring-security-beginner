package com.practice.securitybeginner.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.enums.Role;
import com.practice.securitybeginner.enums.TokenStatus;
import com.practice.securitybeginner.enums.TokenType;
import com.practice.securitybeginner.properties.JwtTokenProperties;
import com.practice.securitybeginner.security.JwtAuthenticationToken;
import com.practice.securitybeginner.security.domain.JwtToken;
import com.practice.securitybeginner.security.domain.JwtTokenClaims;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtTokenProperties.class)
public class JwtTokenUtil {

  private final JwtTokenProperties properties;
  private final ObjectMapper objectMapper;
  private final ConvertUtil convertUtil;

  public String generateToken(JwtToken token) {
    ZoneId zone = ZoneId.systemDefault();
    JwtTokenClaims tokenClaims = token.getClaims();
    return Jwts.builder()
      .subject(token.getUserId())
      .claims(convertUtil.convertObjectToMap(tokenClaims))
      .issuedAt(Date.from(token.getIssuedAt().atZone(zone).toInstant()))
      .expiration(Date.from(token.getExpiresAt().atZone(zone).toInstant()))
      .signWith(getSigningKey())
      .compact();
  }

  public JwtToken parse(String tokenValue) {
    Claims claims = extractAllClaims(tokenValue);
    return JwtToken.builder()
      .userId(claims.getSubject())
      .type(extractTokenType(claims))
      .value(tokenValue)
      .status(confirmTokenStatus(claims))
      .claims(JwtTokenClaims.from(claims, objectMapper))
      .issuedAt(convertToLocalDateTime(claims.getIssuedAt()))
      .expiresAt(convertToLocalDateTime(claims.getExpiration()))
      .build();
  }

  private Claims extractAllClaims(String tokenValue) {
    return Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(tokenValue)
      .getPayload();
  }

  private TokenType extractTokenType(Claims claims) {
    String tokenType = claims.get("type", String.class);
    return TokenType.valueOf(tokenType);
  }

  private TokenStatus confirmTokenStatus(Claims claims) {
    if (claims.getExpiration().before(new Date())) {
      return TokenStatus.EXPIRED;
    }
    return TokenStatus.ACTIVE;
  }

  private LocalDateTime convertToLocalDateTime(Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Base64.getEncoder().encodeToString(properties.getSecretKey().getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

}
