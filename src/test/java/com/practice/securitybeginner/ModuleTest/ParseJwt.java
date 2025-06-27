package com.practice.securitybeginner.ModuleTest;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Set;
import java.util.function.Function;

public class ParseJwt {

  @Test
  public void testParseJwt() {
    ObjectMapper mapper = new ObjectMapper();
    String jwt = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbkBuYXZlci5jb20iLCJsYXN0TG9naW5EYXRlVGltZSI6WzIwMjUsMiw3LDksMTksOSwxNzcwMDAwMDBdLCJyb2xlcyI6WyJBRE1JTiIsIlVTRVIiXSwidXNlck5hbWUiOiLthYzsiqTtirgiLCJ1c2VySWQiOiJhZG1pbkBuYXZlci5jb20iLCJjcmVhdGVEYXRlIjpbMjAyNSwyLDddLCJpYXQiOjE3Mzg5MDk0NjYsImV4cCI6MTczODkxMzA2Nn0.nWGtEkuyaY-CsivCmICza9Xt42_qSQJc_3TN1ZhD9zdcdor33Z4mag63nATvThb7";
    Claims claims = extractAllClaims(jwt);
    Set<String> temp = mapper.convertValue(claims.get("roles"), new TypeReference<>() {});
    System.out.println(temp);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Base64.getEncoder().encodeToString("Jzz4p/T4MtBbFsk/d1RyilDTxmElYnPQdlP1YWNrEDo=".getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

}
