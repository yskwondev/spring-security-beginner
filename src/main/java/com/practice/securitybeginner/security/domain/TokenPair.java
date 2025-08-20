package com.practice.securitybeginner.security.domain;

import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.enums.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenPair {

  private JwtToken accessToken;
  private JwtToken refreshToken;

  public static TokenPair from(ApplicationUser user, long accessTokenExpiredTimeMillis, long refreshTokenExpiredTimeMillis) {
    return TokenPair.builder()
      .accessToken(JwtToken.createToken(TokenType.ACCESS, user, accessTokenExpiredTimeMillis))
      .refreshToken(JwtToken.createToken(TokenType.REFRESH, user, refreshTokenExpiredTimeMillis))
      .build();
  }

}
