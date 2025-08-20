package com.practice.securitybeginner.security.service.impl;

import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.interceptor.exception.AuthenticateException;
import com.practice.securitybeginner.properties.JwtTokenProperties;
import com.practice.securitybeginner.security.domain.JwtToken;
import com.practice.securitybeginner.security.domain.TokenPair;
import com.practice.securitybeginner.security.service.JwtTokenService;
import com.practice.securitybeginner.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import static com.practice.securitybeginner.enums.ErrorCode.INVALID_TOKEN;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {

  private final JwtTokenProperties properties;
  private final JwtTokenUtil jwtTokenUtil;

  @Override
  public TokenPair issueTokenPair(ApplicationUser user) {

    TokenPair tokenPair = TokenPair.from(user, properties.getAccessTokenExpiredTime().toMillis(), properties.getRefreshTokenExpiredTime().toMillis());
    JwtToken accessToken = tokenPair.getAccessToken();
    JwtToken refreshToken = tokenPair.getRefreshToken();
    accessToken.setValue(jwtTokenUtil.generateToken(accessToken));
    refreshToken.setValue(jwtTokenUtil.generateToken(refreshToken));

    // TODO : refresh token Redis 저장

    return tokenPair;
  }

  @Override
  public String issueRefreshTokenCookie(String refreshToken) {

    // set cookie for refresh token
    ResponseCookie refreshTokenCookie = ResponseCookie.from(properties.getRefreshTokenCookieKey(), refreshToken)
        .httpOnly(true)
        .secure(properties.getRefreshTokenCookieSecure())
        .sameSite(properties.getRefreshTokenSameSite())
        .maxAge(properties.getRefreshTokenExpiredTime().toSeconds())  // 쿠키 유효기간
        .path(properties.getReIssueUrl())  // refresh token 쿠키가 포함될 경로
        .domain(properties.getRefreshTokenDomain())
        .build();

    return refreshTokenCookie.toString();
  }

  @Override
  public JwtToken reissueAccessToken(String refreshTokenValue) {

    // TODO : refresh token 유효성 검증 후 객체 반환으로 변경 (Redis에서 찾기)
    JwtToken refreshToken = jwtTokenUtil.parse(refreshTokenValue);

    JwtToken newAccessToken = refreshToken.reissueAccessToken(properties.getAccessTokenExpiredTime().toMillis());

    newAccessToken.setValue(jwtTokenUtil.generateToken(newAccessToken));

    return newAccessToken;
  }

  @Override
  public boolean validateToken(String tokenValue) {
    return jwtTokenUtil.parse(tokenValue).isValid();
  }

  @Override
  public ApplicationUser getUserFromToken(String tokenValue) {
    JwtToken token = jwtTokenUtil.parse(tokenValue);
    if (!token.isValid()) {
      throw new AuthenticateException(INVALID_TOKEN);
    }
    return ApplicationUser.from(token);
  }

  @Override
  public void revokeToken(String tokenValue) {
    JwtToken token = jwtTokenUtil.parse(tokenValue);
    token.invalidate();

    // TODO : token 무효화 시, refresh token이라면 Redis에서 삭제
  }

  @Override
  public void revokeTokenByUserId(String userId) {
    // TODO : user token 무효화 시, refresh token이라면 Redis에서 삭제

  }

}
