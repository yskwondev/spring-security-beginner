package com.practice.securitybeginner.security.service;

import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.security.domain.JwtToken;
import com.practice.securitybeginner.security.domain.TokenPair;

public interface JwtTokenService {

  TokenPair issueTokenPair(ApplicationUser user);

  JwtToken reissueAccessToken(String refreshTokenValue);

  boolean validateToken(String token);

  ApplicationUser getUserFromToken(String token);

  void revokeToken(String token);

  void revokeTokenByUserId(String userId);

  String issueRefreshTokenCookie(String refreshToken);

}
